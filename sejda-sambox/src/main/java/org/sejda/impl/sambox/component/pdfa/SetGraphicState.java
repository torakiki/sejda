/*
 * Created on 24/07/24
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
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.sejda.sambox.contentstream.operator.OperatorName.SET_GRAPHICS_STATE_PARAMS;

/**
 * @author Andrea Vacondio
 */
public class SetGraphicState extends PdfAContentStreamOperator {

    private static final Logger LOG = LoggerFactory.getLogger(ShadingFill.class);

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase operand = operands.get(0);
        if (operand instanceof COSName gsName) {
            COSDictionary extGState = ofNullable(
                    gsResources().getDictionaryObject(gsName, COSDictionary.class)).orElseThrow(
                    () -> new MissingResourceException("Missing ExtGState dictionary: " + gsName.getName()));
            if (!(extGState instanceof InUseDictionary)) {
                new ExtGStateSanitizer(conversionContext()).sanitizeExtGState(extGState);
                hit(gsName, extGState);
                LOG.trace("Hit Ext Graphic State with name {}", gsName.getName());
            }
        }
    }

    private void hit(COSName objectName, COSDictionary extGState) throws IOException {
        COSDictionary extGStates = gsResources();
        if (!(extGStates.getItem(objectName) instanceof InUseDictionary)) {
            extGStates.setItem(objectName, new InUseDictionary(extGState));
        }
    }

    private COSDictionary gsResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.EXT_G_STATE, k -> new COSDictionary(), COSDictionary.class);
    }

    @Override
    public String getName() {
        return SET_GRAPHICS_STATE_PARAMS;
    }
}
