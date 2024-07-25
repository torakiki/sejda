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

import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;

import java.io.IOException;
import java.util.List;

/**
 * Rule 6.2.10 of ISO 19005-1: NOTE 2 In earlier versions of the PDF format a PostScript operator PS was defined. As this operator is not defined in
 * PDF Reference its use is implicitly prohibited by 6.2.10.
 *
 * @author Andrea Vacondio
 */
public class PostScriptOperator extends OperatorProcessor {

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        throw new IOException("Invalid PS operator");
    }

    @Override
    public String getName() {
        return "PS";
    }
}
