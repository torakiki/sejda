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

import org.sejda.impl.sambox.component.FailableContentStreamProcessor;
import org.sejda.impl.sambox.component.optimization.ResourcesHitter.SetGraphicState;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.state.Concatenate;
import org.sejda.sambox.contentstream.operator.state.Restore;
import org.sejda.sambox.contentstream.operator.state.Save;
import org.sejda.sambox.contentstream.operator.state.SetMatrix;
import org.sejda.sambox.cos.COSBase;

import java.io.IOException;
import java.util.List;

import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * @author Andrea Vacondio
 */
public class PdfAContentStreamProcessor extends FailableContentStreamProcessor {

    private final ConversionContext context;

    public PdfAContentStreamProcessor(ConversionContext context) {
        requireNotNullArg(context, "Conversion context cannot be null");
        this.context = context;
        addOperator(new Concatenate());
        addOperator(new SetGraphicState());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
    }

    final void addOptimizationOperator(PdfAContentStreamOperator op) {
        op.setConversionContext(context);
        addOperator(op);
    }

    final boolean addOptimizationOperatorIfAbsent(PdfAContentStreamOperator op) {
        op.setConversionContext(context);
        return addOperatorIfAbsent(op);
    }

    @Override
    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException {
        //TODO make sure there are valid operators only, according to PDFA spec
    }
}
