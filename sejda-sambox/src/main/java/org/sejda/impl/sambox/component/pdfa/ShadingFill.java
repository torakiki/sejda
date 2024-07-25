/*
 * Created on 22/07/24
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

import org.sejda.impl.sambox.component.optimization.InUseDictionary;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorName;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * Operator that adds a a default color space to the current resource dictionary if necessary
 *
 * @author Andrea Vacondio
 */
public class ShadingFill extends PdfAContentStreamOperator {

    private static final Logger LOG = LoggerFactory.getLogger(ShadingFill.class);

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase operand = operands.get(0);
        if (operand instanceof COSName objectName) {
            COSDictionary shading = ofNullable(
                    shadingResources().getDictionaryObject(objectName, COSDictionary.class)).orElseThrow(
                    () -> new MissingResourceException("Missing shading dictionary: " + objectName.getName()));
            if (!(shading instanceof InUseDictionary)) {
                // always mark as hit
                hit(objectName, shading);
                LOG.trace("Hit shading with name {}", objectName.getName());
                conversionContext().maybeAddDefaultColorSpaceFor(shading.getDictionaryObject(COSName.CS),
                        csResources());
                var extGState = shading.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class);
                if (nonNull(extGState)) {
                    new ExtGStateSanitizer(conversionContext()).sanitizeExtGState(extGState);
                }
            }
        }
    }

    private void hit(COSName objectName, COSDictionary shading) throws IOException {
        COSDictionary shadings = shadingResources();
        if (!(shadings.getItem(objectName) instanceof InUseDictionary)) {
            shadings.setItem(objectName, new InUseDictionary(shading));
        }
    }

    private COSDictionary shadingResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.SHADING, k -> new COSDictionary(), COSDictionary.class);
    }

    private COSDictionary csResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.COLORSPACE, k -> new COSDictionary(), COSDictionary.class);
    }

    @Override
    public String getName() {
        return OperatorName.SHADING_FILL;
    }
}
