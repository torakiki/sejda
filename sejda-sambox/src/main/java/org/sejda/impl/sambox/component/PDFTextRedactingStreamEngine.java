/*
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import org.sejda.model.TopLeftRectangularBox;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.color.*;
import org.sejda.sambox.cos.*;
import org.sejda.sambox.output.ContentStreamWriter;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.common.PDStream;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.color.PDColor;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.state.PDTextState;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;
import org.sejda.sambox.text.PDFTextStreamEngine;
import org.sejda.sambox.text.TextPosition;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.sejda.io.CountingWritableByteChannel.from;

/**
 * Given a bounding box, it removes all show text operators that write text within that bounding box.
 * Provides font and text metrics about the redacted text in the context found.
 */
public class PDFTextRedactingStreamEngine extends PDFTextStreamEngine {

    private static final Logger LOG = LoggerFactory.getLogger(PDFTextRedactingStreamEngine.class);

    public StringBuilder redactedString = new StringBuilder();
    public PDFont redactedFont;
    public float redactedFontSize;
    public PDColor redactedFontColor;
    public Point redactedTextPosition;
    public RenderingMode redactedTextRenderingMode;

    private StringBuilder lastShownString = new StringBuilder();
    private float lastShownStringFirstX = 0;
    private float lastShownStringLastX = 0;

    private boolean matchesRedactionFilter = false;
    private boolean matchesRedactionFilterPartially = false;
    private List<COSBase> filteredOperands = new ArrayList<>();
    private PDStream filteredStream;
    private ContentStreamWriter filteredStreamWriter;

    private Set<IndirectCOSObjectIdentifier> redactedFormXObjectIds = new HashSet<>();

    private TopLeftRectangularBox box;


    public PDFTextRedactingStreamEngine(TopLeftRectangularBox box) throws IOException {
        super();
        // process commands that change the color of text
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetNonStrokingDeviceRGBColor());

        this.box = box;

        this.filteredStream = new PDStream();
        this.filteredStreamWriter = new ContentStreamWriter(from(filteredStream.createOutputStream(COSName.FLATE_DECODE)));
    }

    public PDStream getFilteredStream() {
        return filteredStream;
    }

    @Override
    public void processPage(PDPage page) throws IOException
    {
        super.processPage(page);
        org.sejda.util.IOUtils.close(this.filteredStreamWriter);

        // replace the page stream
        COSArray array = new COSArray();
        array.add(getFilteredStream());
        page.getCOSObject().setItem(COSName.CONTENTS, array);
    }

    @Override
    public void showForm(PDFormXObject form) throws IOException
    {
        // save
        PDStream tmpStream = this.filteredStream;
        ContentStreamWriter tmpWriter = this.filteredStreamWriter;

        String tmpRedactedString = this.redactedString.toString();

        try {
            this.filteredStream = new PDStream();
            this.filteredStreamWriter = new ContentStreamWriter(from(filteredStream.createOutputStream(COSName.FLATE_DECODE)));

            // find name of XForm
            PDResources resources = getResources();
            COSName existingFormName = findNameOf(resources, form);
            if(existingFormName == null) {
                // fallback to current page resources
                resources = getCurrentPage().getResources();
                existingFormName = findNameOf(resources, form);
            }

            if (existingFormName == null) throw new RuntimeException("Could not find form in page resources");

            LOG.debug("Processing FormXObject {} ({})", existingFormName, form);

            super.showForm(form);
            org.sejda.util.IOUtils.close(this.filteredStreamWriter);

            boolean formWasRedacted = !tmpRedactedString.equals(this.redactedString.toString());

            if(formWasRedacted) {
                LOG.debug("Redaction occurred in FormXObject {} ({})", existingFormName, form);

                // replace the form stream
                PDFormXObject newForm = new PDFormXObject(getFilteredStream().getCOSObject());
                newForm.setMatrix(form.getMatrix().createAffineTransform());
                newForm.setFormType(form.getFormType());
                newForm.setBBox(form.getBBox());
                newForm.setResources(form.getResources());
                newForm.setStructParents(form.getStructParents());

                // points to the same object id
                newForm.getCOSObject().getCOSObject().idIfAbsent(form.getCOSObject().getCOSObject().id());

                LOG.debug("Updating FormXObject {} with {}", existingFormName, newForm);

                resources.put(existingFormName, newForm);
                resources.getResourceCache().put(newForm.getCOSObject().getCOSObject().id().objectIdentifier, newForm);

                // keep track of which forms were already redacted
                redactedFormXObjectIds.add(newForm.getCOSObject().getCOSObject().id());
            }

            LOG.debug("Done processing FormXObject {}", form);
        } finally {
            // restore
            this.filteredStream = tmpStream;
            this.filteredStreamWriter = tmpWriter;
        }
    }

    private COSName findNameOf(PDResources resources, PDXObject item) throws IOException {
        for (COSName name : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(name);
            if (xObject == item) {
                return name;
            }
        }

        return null;
    }

    @Override
    public void showTextStrings(COSArray array) throws IOException
    {
        PDTextState textState = getGraphicsState().getTextState();
        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100f;
        PDFont font = textState.getFont();
        boolean isVertical = ofNullable(font).map(f -> f.isVertical()).orElse(false);

        List<COSBase> filteredParams = new ArrayList<>();
        this.matchesRedactionFilterPartially = false;

        for (COSBase obj : array)
        {
            if (obj instanceof COSNumber)
            {
                float tj = ((COSNumber) obj).floatValue();

                // calculate the combined displacements
                float tx, ty;
                if (isVertical)
                {
                    tx = 0;
                    ty = -tj / 1000 * fontSize;
                }
                else
                {
                    tx = -tj / 1000 * fontSize * horizontalScaling;
                    ty = 0;
                }

                applyTextAdjustment(tx, ty);
                filteredParams.add(obj);
            }
            else if (obj instanceof COSString)
            {
                // Handling scenarios where we want to edit part of a show text operation
                // EG: the show text operation displays "This is a sentence" but the user wants to redact/replace "This is"
                // Not all text would be redacted out, instead of only the partial match.

                this.matchesRedactionFilter = false;
                this.lastShownString = new StringBuilder();

                byte[] string = ((COSString) obj).getBytes();
                showText(string);

                if(this.matchesRedactionFilter) {
                    this.matchesRedactionFilterPartially = true;

                    float redactedStringWidth = 0;
                    try {
                        redactedStringWidth = font.getStringWidth(this.lastShownString.toString());
                    } catch (IllegalArgumentException e) {
                        // can fail with java.lang.IllegalArgumentException: U+0041 is not available in this font's encoding: built-in (TTF)
                        // we then fallback to calculating the string width based on what the text position reports
                        // the text position metrics can be inaccurate, so it's not the main source of info
                        redactedStringWidth = this.lastShownStringLastX - this.lastShownStringFirstX;
                    }
                    float filteredTj = - (redactedStringWidth / horizontalScaling );

                    filteredParams.add(new COSFloat(filteredTj));
                } else {
                    filteredParams.add(obj);
                }
            }
            else
            {
                throw new IOException("Unknown type in array for TJ operation:" + obj);
            }
        }

        if(this.matchesRedactionFilterPartially) {
            this.matchesRedactionFilter = true;
            this.filteredOperands = new ArrayList<>();
            COSArray cosArray = new COSArray();
            cosArray.addAll(filteredParams);
            this.filteredOperands.add(cosArray);
        }
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        boolean skip = false;
        matchesRedactionFilter = false;
        matchesRedactionFilterPartially = false;
        this.filteredOperands = new ArrayList<>();

        //System.out.println(operands.toString() + " " + operator.toString());

        super.processOperator(operator, operands);

        if(operator.getName().equals("TD") || operator.getName().equals("\"") || operator.getName().equals("'") ||
                operator.getName().equals("T*")) {
            skip = true;
        }

        if(matchesRedactionFilter || skip) {
            if(this.matchesRedactionFilterPartially && this.filteredOperands.size() > 0) {
                LOG.debug("Partially filtering out current text operator: {}", operator.getName());

                LOG.debug("Writing operands: {}", this.filteredOperands);
                LOG.debug("Writing operator: {}", operator.getName());

                filteredStreamWriter.writeTokens(new ArrayList<Object>(this.filteredOperands));
                filteredStreamWriter.writeTokens(operator);
            } else {
                LOG.debug("Filtering out current text operator: {}", operator.getName());
            }
        } else {
            //LOG.debug("Writing operands: {}", operands);
            //LOG.debug("Writing operator: {}", operator.getName());

            filteredStreamWriter.writeTokens(new ArrayList<Object>(operands));
            filteredStreamWriter.writeTokens(operator);
        }
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        float x = text.getXDirAdj();
        float y = text.getYDirAdj();

        boolean inBoundingBox = box.containsPoint(x, y) && box.containsPoint(x, y - text.getHeightDir()) &&
                box.containsPoint(x + text.getWidthDirAdj(), y);

        if (inBoundingBox) {
            // current Text operator should be filtered out
            matchesRedactionFilter = true;

            redactedString.append(text.getUnicode());
            redactedFont = text.getFont();

            if(lastShownString.length() == 0){
                lastShownStringFirstX = x;
            }
            lastShownString.append(text.getUnicode());
            lastShownStringLastX = x + text.getWidthDirAdj();

            redactedFontSize = text.getYScale();
            redactedFontColor = getGraphicsState().getNonStrokingColor();

            if(redactedTextPosition == null) {
                LOG.debug("Position: {},{}", text.getX(), text.getY());

                //LOG.debug("fontSize {} fontSizePt {} yScale {}, maxHeight {}", text.getFontSize(), text.getFontSizeInPt(), text.getYScale(), text.getHeight());
                redactedTextPosition = new Point((int)x, (int)y);
                redactedTextRenderingMode = getGraphicsState().getTextState().getRenderingMode();
            } else {
                // sometimes words aren't in left to right order in the text stream
                // Order date might be written "date" then "Order"
                // check if the current position is more to the left, replace if so
                if(redactedTextPosition.getX() > x) {
                    LOG.debug("Found more to the left position: {},{}", text.getX(), text.getY());

                    redactedTextPosition = new Point((int)x, (int)y);
                    redactedTextRenderingMode = getGraphicsState().getTextState().getRenderingMode();
                }
            }
        }
    }

    private void logMatrix(String which, Matrix matrix) {
        LOG.debug("Matrix {}: scale {},{}  scaling {},{}  translate {},{}  shear {},{}", which,
                matrix.getScaleX(), matrix.getScaleY(),
                matrix.getScalingFactorX(), matrix.getScalingFactorY(),
                matrix.getTranslateX(), matrix.getTranslateY(),
                matrix.getShearX(), matrix.getShearY()
        );
    }

    public Set<IndirectCOSObjectIdentifier> getRedactedFormXObjectIds() {
        return redactedFormXObjectIds;
    }
}
