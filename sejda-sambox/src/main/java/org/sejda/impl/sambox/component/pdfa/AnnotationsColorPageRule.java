/*
 * Created on 11/07/24
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

import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.color.PDDeviceCMYK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Objects.nonNull;

/**
 * Rule that, if necessary, converts C and IC values in annotations dictionaries to a color space that is handled by the OutputIntent of the document.
 * <p>
 * Ex. if the output intent is a CMYK (4 components) and the /C value has 3 elements (DeviceRGB),
 * it converts the 3 elements array (RGB) to a 4 elements array (CMYK)
 *
 * @author Andrea Vacondio
 */
//TODO make sure this is used once hasCorICAnnotationKey is filled and outputIntent is also filled
public class AnnotationsColorPageRule extends BaseRule<PDPage, TaskException> {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationsColorPageRule.class);

    public AnnotationsColorPageRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDPage page) throws TaskException {
        if (conversionContext().hasCorICAnnotationKey()) {
            var annotations = page.getCOSObject().getDictionaryObject(COSName.ANNOTS, COSArray.class);
            if (nonNull(annotations)) {
                for (int i = 0; i < annotations.size(); i++) {
                    var annotation = annotations.getObject(i, COSDictionary.class);
                    maybeConvert(annotation, COSName.C);
                    maybeConvert(annotation, COSName.IC);
                }
            }
        }
    }

    private void maybeConvert(COSDictionary annotation, COSName key) throws TaskExecutionException {
        var color = annotation.getDictionaryObject(key, COSArray.class);
        if (nonNull(color)) {
            var newColors = new COSArray();
            try {
                newColors.setFloatArray(convert(color.toFloatArray()));
                annotation.setItem(key, newColors);
            } catch (IOException e) {
                LOG.warn("Unable to convert the annotation " + key + " color to the expect output intent color space",
                        e);
                conversionContext().maybeRemoveForbiddenKeys(annotation, "annotation", key);
            }
        }
    }

    /**
     * Converts RGB -> CMYK or vice versa if needed
     *
     * @param input
     * @return
     * @throws IOException
     */
    private float[] convert(float[] input) throws IOException {
        if (conversionContext().outputIntent().profile().components() == 3 && input.length == 4) {
            return PDDeviceCMYK.INSTANCE.toRGB(input);
        }
        if (conversionContext().outputIntent().profile().components() == 4 && input.length == 3) {
            return conversionContext().outputIntent().profile().colorSpace().fromRGB(input);
        }
        return input;
    }

}
