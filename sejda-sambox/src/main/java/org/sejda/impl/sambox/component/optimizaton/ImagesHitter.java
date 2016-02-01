/*
 * Created on 30 gen 2016
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
package org.sejda.impl.sambox.component.optimizaton;

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDTransparencyGroup;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that parses the page content steam and the page annotations appearance stream, wraps any image xobject (type xobject, subtype image) found in an instance of
 * {@link ReadOnlyFilteredCOSStream} and puts it back to the resource dictionary. It's then easy to identify later xobjects in use by the page/s and what can be discarded.
 * 
 * @author Andrea Vacondio
 *
 */
public class ImagesHitter extends PDFStreamEngine implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesHitter.class);

    public ImagesHitter() {
        addOperator(new XObjectOperator());
    }

    private class XObjectOperator extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.size() < 1) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {

                COSName objectName = (COSName) operand;
                COSBase existing = ofNullable(
                        context.getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT))
                                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                                .map(d -> d.getDictionaryObject(objectName)).orElseThrow(
                                        () -> new MissingResourceException("Missing XObject: " + objectName.getName()));

                if (!(existing instanceof ReadOnlyFilteredCOSStream)) {
                    PDXObject xobject = PDXObject.createXObject(existing.getCOSObject(), context.getResources());
                    if (xobject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xobject;
                        LOG.trace("Hit image with name {}", objectName.getName());
                        // we wrap the existing so we can identify it later as "in use" and already processed
                        ReadOnlyFilteredCOSStream optimizedImage = ReadOnlyFilteredCOSStream
                                .readOnly(image.getCOSStream());
                        COSDictionary resources = context.getResources().getCOSObject();
                        COSDictionary xobjects = ofNullable(resources.getDictionaryObject(COSName.XOBJECT))
                                .filter(b -> b instanceof COSDictionary).map(b -> (COSDictionary) b).orElseGet(() -> {
                                    COSDictionary ret = new COSDictionary();
                                    resources.setItem(COSName.XOBJECT, ret);
                                    return ret;
                                });
                        xobjects.setItem(objectName, optimizedImage);
                    } else if (xobject instanceof PDTransparencyGroup) {
                        context.showTransparencyGroup((PDTransparencyGroup) xobject);
                    } else if (xobject instanceof PDFormXObject) {
                        context.showForm((PDFormXObject) xobject);
                    }
                }
            }
        }

        @Override
        public String getName() {
            return "Do";
        }
    }

    @Override
    public void accept(PDPage page) {
        try {
            this.processPage(page);
            for (PDAnnotation annotation : page.getAnnotations()) {
                this.showAnnotation(annotation);
            }
        } catch (IOException e) {
            LOG.warn("Failed parse page, skipping and continuing with next.", e);
        }
    }

}
