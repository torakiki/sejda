/*
 * Created on 16/10/24
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

import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorName;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSString;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrea Vacondio
 */
public class ShowTextAdjusted extends PdfAContentStreamOperator {

    public void process(Operator operator, List<COSBase> arguments) {
        if (!arguments.isEmpty() && RenderingMode.NEITHER != getContext().getGraphicsState().getTextState()
                .getRenderingMode()) {
            if (arguments.getFirst() instanceof COSArray cosArray) {
                Optional.ofNullable(conversionContext().currentFont()).ifPresent(
                        f -> cosArray.stream().filter(v -> v instanceof COSString).map(COSString.class::cast)
                                .forEach(f::addString));
            }
        }
    }

    @Override
    public String getName() {
        return OperatorName.SHOW_TEXT_ADJUSTED;
    }
}