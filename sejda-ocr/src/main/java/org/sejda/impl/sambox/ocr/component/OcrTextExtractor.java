/*
 * Created on 03 dic 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.ocr.component;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDStream;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDTransparencyGroup;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stream engine doing OCR on images of a PDPage
 * 
 * @author Andrea Vacondio
 */
public class OcrTextExtractor extends PDFStreamEngine implements Consumer<PDPage>, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(OcrTextExtractor.class);

    private Writer writer;
    private OCR ocrEngine;

    public OcrTextExtractor(Writer writer, OCR ocrEngine) {
        requireNotNullArg(writer, "Cannot write text on a null writer");
        requireNotNullArg(ocrEngine, "OCR engine cannot be null");
        addOperator(new DoOCR());
        this.writer = writer;
        this.ocrEngine = ocrEngine;
        // so it uses the env variable TESSDATA_PREFIX
        this.ocrEngine.setDatapath(null);
    }

    public void setLanguage(Set<Locale> languages) {
        if (nonNull(languages) && !languages.isEmpty()) {
            ocrEngine.setLanguage(languages.stream().map(Locale::getISO3Language).collect(Collectors.joining("+")));
        } else {
            // default to eng
            ocrEngine.setLanguage("eng");
        }
    }

    private class DoOCR extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.isEmpty()) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {

                COSName name = (COSName) operand;
                COSBase existing = ofNullable(
                        getContext().getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT,
                                COSDictionary.class))
                                .map(d -> d.getDictionaryObject(name))
                                .orElseThrow(() -> new MissingResourceException("Missing XObject: " + name.getName()));
                if (existing instanceof COSStream) {
                    COSStream stream = (COSStream) existing;
                    String subtype = stream.getNameAsString(COSName.SUBTYPE);
                    if (COSName.IMAGE.getName().equals(subtype)) {
                        LOG.trace("Performing OCR on {}", name);
                        PDXObject xobject = PDXObject.createXObject(stream.getCOSObject(), getContext().getResources());
                        try {
                            OcrTextExtractor.this.writer
                                    .write(ocrEngine.ocrTextFrom(((PDImageXObject) xobject).getImage()));
                        } catch (IOException e) {
                            LOG.warn("Unable to OCR image", e);
                        }
                        xobject.getCOSObject().unDecode();
                    } else if (COSName.FORM.getName().equals(subtype)) {
                        PDXObject xobject = PDXObject.createXObject(existing.getCOSObject(),
                                getContext().getResources());
                        if (xobject instanceof PDTransparencyGroup) {
                            getContext().showTransparencyGroup((PDTransparencyGroup) xobject);
                        } else if (xobject instanceof PDFormXObject) {
                            getContext().showForm((PDFormXObject) xobject);
                        }
                    }
                }
            }
        }

        @Override
        public String getName() {
            return "Do";
        }
    }

    /**
     * process the page
     * 
     * @throws UnsatisfiedLinkError
     *             in case the OCR engine is not found
     */
    @Override
    public void accept(PDPage page) {
        try {
            if (page.hasContents()) {
                processPage(page);
                unload(page);
            } else {
                LOG.debug("Skipping page with no content");
            }
        } catch (IOException e) {
            LOG.error("An error occurred doing OCR on page, skipping and continuing with next.", e);
        }
    }

    private void unload(PDPage page) {
        Iterator<PDStream> iter = page.getContentStreams();
        while (iter.hasNext()) {
            iter.next().getCOSObject().unDecode();
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.writer);
    }
}
