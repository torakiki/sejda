/*
 * Created on 25/07/24
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
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.font.PDFontFactory;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.require;
import static org.sejda.commons.util.RequireUtils.requireIOCondition;
import static org.sejda.sambox.contentstream.operator.OperatorName.SET_FONT_AND_SIZE;

/**
 * @author Andrea Vacondio
 */
public class SetFontOperator extends PdfAContentStreamOperator {

    private final Map<IndirectCOSObjectIdentifier, InUseDictionary> hitFontsById = new HashMap<>();

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        require(operands.size() > 1, () -> new MissingOperandException(operator, operands));

        COSBase operand = operands.get(0);
        if (operand instanceof COSName fontName) {
            COSDictionary fontDictionary = ofNullable(
                    fontResources().getDictionaryObject(fontName, COSDictionary.class)).orElseThrow(
                    () -> new MissingResourceException("Missing font dictionary: " + fontName.getName()));
            //required in the spec
            fontDictionary.setItem(COSName.TYPE, COSName.FONT);
            if (!(fontDictionary instanceof InUseDictionary)) {
                var font = PDFontFactory.createFont(fontDictionary, getContext().getResources().getResourceCache());
                requireIOCondition(
                        font.isEmbedded() || RenderingMode.NEITHER == getContext().getGraphicsState().getTextState()
                                .getRenderingMode(), "The font " + fontName.getName() + " is not embedded");
            }

        }
    }

    private COSDictionary fontResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.FONT, k -> new COSDictionary(), COSDictionary.class);
    }

    @Override
    public String getName() {
        return SET_FONT_AND_SIZE;
    }
}
