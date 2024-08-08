/*
 * Created on 16/07/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.model.image.ImageType;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.require;
import static org.sejda.commons.util.RequireUtils.requireIOCondition;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream.readOnly;
import static org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream.readOnlyJpegImage;
import static org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream.readOnlyPngImage;
import static org.sejda.sambox.contentstream.operator.OperatorName.DRAW_OBJECT;
import static org.sejda.sambox.pdmodel.graphics.PDXObject.createXObject;
import static org.sejda.sambox.pdmodel.graphics.image.JPEGFactory.getColorSpaceFromAWT;

/**
 * Operator on xobjects that covers some of the constraints in chapter 6.2 of ISO 19005-1.
 *
 * @author Andrea Vacondio
 */
public class XObjectOperator extends OperatorProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(XObjectOperator.class);

    private final ConversionContext conversionContext;

    public XObjectOperator(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {

        require(!operands.isEmpty(), () -> new MissingOperandException(operator, operands));

        if (operands.getFirst() instanceof COSName objectName) {

            COSStream existing = ofNullable(getContext().getResources().getCOSObject()
                    .getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).map(
                            d -> d.getDictionaryObject(objectName, COSStream.class))
                    .orElseThrow(() -> new MissingResourceException("Missing XObject: " + objectName.getName()));
            processStream(objectName, existing);
        }
    }

    private void processStream(COSName objectName, COSStream stream) throws IOException {
        if (!(stream instanceof ReadOnlyFilteredCOSStream)) {
            // always mark as hit
            hit(objectName, stream);
            var subtype = stream.getCOSName(COSName.SUBTYPE);
            LOG.trace("Hit image with name {} and type {}", objectName.getName(), subtype);
            if (COSName.IMAGE.equals(subtype)) {
                conversionContext.maybeRemoveForbiddenKeys(stream, "XObject", IOException::new,
                        COSName.getPDFName("Alternates"), COSName.getPDFName("OPI"));
                sanitizeInterpolateValue(stream);
                conversionContext.sanitizeRenderingIntents(stream);
                //JPEG2000 and SMASK are not allowed in PDFa/1b so we convert to jpg
                if (stream.hasFilter(COSName.JPX_DECODE) || nonNull(stream.getDictionaryObject(COSName.SMASK))) {
                    conversionContext.maybeFailOnInvalidElement(
                            () -> new IOException("Found an JPEG2000 image or an image with SMASK"));
                    //TODO make sure we don't compress the same image multiple times
                    PDImageXObject image = (PDImageXObject) createXObject(stream, getContext().getResources());
                    replaceHitXObject(objectName, createFromXObjectImage(image));
                    notifyEvent(conversionContext.notifiableMetadata()).taskWarning(
                            "Image was converted to a supported type");
                }
                conversionContext.maybeAddDefaultColorSpaceFor(stream.getDictionaryObject(COSName.CS), csResources());
            } else if (COSName.FORM.equals(subtype)) {
                //A form XObject dictionary shall not contain any of the following:
                //* the OPI key;
                //* the Subtype2 key with a value of PS;
                //* the PS key;
                //* reference XObject
                conversionContext.maybeRemoveForbiddenKeys(stream, "Form XObject", IOException::new,
                        COSName.getPDFName("Ref"), COSName.getPDFName("OPI"), COSName.PS);
                if (COSName.PS.equals(stream.getCOSName(COSName.getPDFName("Subtype2")))) {
                    conversionContext.maybeRemoveForbiddenKeys(stream, "Form XObject", IOException::new,
                            COSName.getPDFName("Subtype2"));
                }
                PDXObject xobject = createXObject(stream, getContext().getResources());
                getContext().showForm((PDFormXObject) xobject);
            } else {
                throw new IOException("Found image of invalid subtype " + subtype);
            }

            // free up resources used by the underlying COSStream which stores both the filtered and unfiltered bytes[] and DecodeResult potentially creating a large memory
            // footprint
            stream.unDecode();
        }
    }

    private void sanitizeInterpolateValue(COSDictionary image) throws IOException {
        boolean interpolate = image.getBoolean(COSName.INTERPOLATE, false);
        if (interpolate) {
            conversionContext.maybeFailOnInvalidElement(
                    () -> new IOException("Found an image with interpolate value true"));
            image.setBoolean(COSName.INTERPOLATE, false);
            notifyEvent(conversionContext.notifiableMetadata()).taskWarning("Image interpolate value set to false");
        }
    }

    private void replaceHitXObject(COSName objectName, ReadOnlyFilteredCOSStream xObject) {
        xobjectResources().setItem(objectName, xObject);
    }

    private void hit(COSName objectName, COSStream xObject) throws IOException {
        COSDictionary xobjects = xobjectResources();
        if (!(xobjects.getItem(objectName) instanceof ReadOnlyFilteredCOSStream)) {
            xobjects.setItem(objectName, readOnly(xObject));
        }
    }

    private COSDictionary xobjectResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.XOBJECT, k -> new COSDictionary(), COSDictionary.class);
    }

    private COSDictionary csResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.COLORSPACE, k -> new COSDictionary(), COSDictionary.class);
    }

    private ReadOnlyFilteredCOSStream createFromXObjectImage(PDImageXObject image) throws IOException {
        BufferedImage bufferedImage = image.getImage();
        var type =
                bufferedImage.getColorModel().getTransparency() != Transparency.OPAQUE ? ImageType.PNG : ImageType.JPEG;
        var tmpFile = File.createTempFile("tempImage", "." + type.getExtension());
        tmpFile.deleteOnExit();
        ImageIO.write(bufferedImage, type.getExtension(), tmpFile);
        try {
            if (ImageType.PNG.equals(type)) {
                //   var converted = LosslessFactory.createFromImage(bufferedImage);
                return createFromPngFile(tmpFile, image);
            }
            return createFromJpegFile(tmpFile, image);
        } finally {
            bufferedImage.flush();
        }
    }

    private static ReadOnlyFilteredCOSStream createFromPngFile(File file, PDImageXObject original) throws IOException {
        // read image
        var awtImage = ImageIO.read(file);
        requireIOCondition(nonNull(awtImage), "Cannot read image");
        var stream = readOnlyPngImage(file, awtImage.getWidth(), awtImage.getHeight(),
                awtImage.getColorModel().getComponentSize(0), getColorSpaceFromAWT(awtImage));
        stream.setItem(COSName.OC, original.getCOSObject().getDictionaryObject(COSName.OC));
        return stream;
    }

    private static ReadOnlyFilteredCOSStream createFromJpegFile(File file, PDImageXObject original) throws IOException {
        // read image
        var awtImage = ImageIO.read(file);
        requireIOCondition(nonNull(awtImage), "Cannot read image");
        var stream = readOnlyJpegImage(file, awtImage.getWidth(), awtImage.getHeight(),
                awtImage.getColorModel().getComponentSize(0), getColorSpaceFromAWT(awtImage));
        stream.setItem(COSName.OC, original.getCOSObject().getDictionaryObject(COSName.OC));
        return stream;
    }

    @Override
    public String getName() {
        return DRAW_OBJECT;
    }
}
