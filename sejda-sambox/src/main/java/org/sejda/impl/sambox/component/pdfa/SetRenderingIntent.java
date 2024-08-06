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

import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorName;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;

import java.io.IOException;
import java.util.List;

import static org.sejda.commons.util.RequireUtils.require;

/**
 * Rule 6.2.10 of ISO 19005-1: Use of the ri operator shall conform to the rules of 6.2.9.
 *
 * @author Andrea Vacondio
 */
public class SetRenderingIntent extends PdfAContentStreamOperator {

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {

        require(!operands.isEmpty(), () -> new MissingOperandException(operator, operands));

        COSBase operand = operands.get(0);
        if (operand instanceof COSName riName) {
            if (!conversionContext().parameters().conformanceLevel().allowedRenderingIntents()
                    .contains(riName.getName())) {
                throw new IOException("Found invalid rendering intent value " + riName.getName());
            }
        }
    }

    @Override
    public String getName() {
        return OperatorName.SET_RENDERINGINTENT;
    }
}
