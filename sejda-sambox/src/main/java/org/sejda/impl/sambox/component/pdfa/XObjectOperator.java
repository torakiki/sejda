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
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.sambox.contentstream.operator.OperatorName.DRAW_OBJECT;

/**
 * @author Andrea Vacondio
 */
public class XObjectOperator extends PdfAContentStreamOperator {
    private static final Logger LOG = LoggerFactory.getLogger(XObjectOperator.class);

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase operand = operands.get(0);
        if (operand instanceof COSName objectName) {

            COSStream existing = ofNullable(getContext().getResources().getCOSObject()
                    .getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).map(
                            d -> d.getDictionaryObject(objectName, COSStream.class))
                    .orElseThrow(() -> new MissingResourceException("Missing XObject: " + objectName.getName()));
            //TODO hit to avoid processing the same multiple times
            //jpeg2000 convert

        }
    }

    private void processStream(COSName objectName, COSStream stream) throws IOException {
        if (!(stream instanceof ReadOnlyFilteredCOSStream)) {
            // always mark as hit, otherwise will get removed by ResourceDictionaryCleaner
            hit(objectName, stream);
            String subtype = stream.getNameAsString(COSName.SUBTYPE);
            LOG.trace("Hit image with name {} and type {}", objectName.getName(), subtype);
            if (COSName.IMAGE.getName().equals(subtype)) {
                if (stream.hasFilter(COSName.JPX_DECODE)) {
                    //convert to jpg
                }
                conversionContext().maybeRemoveForbiddenKeys(stream, "XObject", IOException::new,
                        COSName.getPDFName("Alternates"), COSName.getPDFName("OPI"));
                sanitizeInterpolateValue(stream);
                sanitizeIntentValue(stream);
                //TODO cechk ColorSpace for device ones and possible update the resources with Default color spaces
            } else if (COSName.FORM.getName().equals(subtype)) {
                //A form XObject dictionary shall not contain any of the following:
                //* the OPI key;
                //* the Subtype2 key with a value of PS;
                //* the PS key;
                //* reference XObject
                conversionContext().maybeRemoveForbiddenKeys(stream, "Form XObject", IOException::new,
                        COSName.getPDFName("Ref"), COSName.getPDFName("OPI"), COSName.PS);
                if ("PS".equals(stream.getNameAsString(COSName.getPDFName("Subtype2")))) {
                    conversionContext().maybeRemoveForbiddenKeys(stream, "Form XObject", IOException::new,
                            COSName.getPDFName("Subtype2"));
                }
                PDXObject xobject = PDXObject.createXObject(stream.getCOSObject(), getContext().getResources());
                getContext().showForm((PDFormXObject) xobject);
            } else {
                throw new IOException("Found image of invalid subtype " + subtype);
            }

            // free up resources used by the underlying COSStream which stores both the filtered and unfiltered bytes[] and DecodeResult potentially creating a large memory
            // footprint
            stream.unDecode();
            LOG.trace("Used memory: {} Mb",
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000);
        }
    }

    private void sanitizeIntentValue(COSStream stream) throws IOException {
        var intent = stream.getNameAsString(COSName.INTENT);
        if (nonNull(intent) && !conversionContext().parameters().conformanceLevel().allowedRenderingIntents()
                .contains(intent)) {
            conversionContext().maybeFailOnInvalidElement(
                    () -> new IOException("Found image with invalid intent value " + intent));
            stream.setItem(COSName.INTENT, COSName.RELATIVE_COLORIMETRIC);
            notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                    "Invalid image intent set to " + COSName.RELATIVE_COLORIMETRIC.getName());
        }
    }

    private void sanitizeInterpolateValue(COSDictionary image) throws IOException {
        boolean interpolate = image.getBoolean(COSName.INTERPOLATE, false);
        if (interpolate) {
            conversionContext().maybeFailOnInvalidElement(
                    () -> new IOException("Found an image with interpolate value true"));
            image.setBoolean(COSName.INTERPOLATE, false);
            notifyEvent(conversionContext().notifiableMetadata()).taskWarning("Image interpolate value set to false");
        }
    }

    private void replaceHitXObject(COSName objectName, ReadOnlyFilteredCOSStream xObject) {
        xobjectResources().setItem(objectName, xObject);
    }

    private void hit(COSName objectName, COSStream xObject) throws IOException {
        COSDictionary xobjects = xobjectResources();
        if (!(xobjects.getItem(objectName) instanceof ReadOnlyFilteredCOSStream)) {
            xobjects.setItem(objectName, ReadOnlyFilteredCOSStream.readOnly(xObject));
        }
    }

    private COSDictionary xobjectResources() {
        COSDictionary resources = getContext().getResources().getCOSObject();
        return ofNullable(resources.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).orElseGet(() -> {
            COSDictionary ret = new COSDictionary();
            resources.setItem(COSName.XOBJECT, ret);
            return ret;
        });
    }

    @Override
    public String getName() {
        return DRAW_OBJECT;
    }
}
