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

import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorName;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.MissingResourceException;

import java.io.IOException;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Andrea Vacondio
 */
class BaseSetColorSpace extends PdfAContentStreamOperator {

    private final String operator;

    public BaseSetColorSpace(String operator) {
        this.operator = operator;
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {

        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase operand = operands.getFirst();
        if (operand instanceof COSName objectName) {
            COSBase cs = ofNullable(csResources().getDictionaryObject(objectName)).orElseThrow(
                    () -> new MissingResourceException("Missing color space: " + objectName.getName()));
            conversionContext().maybeAddDefaultColorSpaceFor(cs, csResources());

        }
    }

    private COSDictionary csResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.COLORSPACE, k -> new COSDictionary(), COSDictionary.class);
    }

    @Override
    public String getName() {
        return this.operator;
    }

    static PdfAContentStreamOperator nonStrokingColorspace() {
        return new BaseSetColorSpace(OperatorName.NON_STROKING_COLORSPACE);
    }

    static PdfAContentStreamOperator strokingColorspace() {
        return new BaseSetColorSpace(OperatorName.STROKING_COLORSPACE);
    }
}
