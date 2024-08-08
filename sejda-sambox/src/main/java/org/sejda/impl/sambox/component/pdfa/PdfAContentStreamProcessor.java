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
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.state.Concatenate;
import org.sejda.sambox.contentstream.operator.state.Restore;
import org.sejda.sambox.contentstream.operator.state.Save;
import org.sejda.sambox.contentstream.operator.state.SetMatrix;
import org.sejda.sambox.contentstream.operator.text.SetTextRenderingMode;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.sejda.commons.util.RequireUtils.requireIOCondition;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;
import static org.sejda.impl.sambox.component.optimization.TilingPatternHitterOperator.tilingPatternSetNonStrokingColor;
import static org.sejda.impl.sambox.component.optimization.TilingPatternHitterOperator.tilingPatternSetStrokingColor;

/**
 * @author Andrea Vacondio
 */
public class PdfAContentStreamProcessor extends FailableContentStreamProcessor {

    private static final Set<String> VALID_OPERATORS = Set.of("b", "B", "b*", "B*", "BDC", "BI", "BMC", "BT", "BX", "c",
            "cm", "CS", "cs", "d", "d0", "d1", "Do", "DP", "EI", "EMC", "ET", "EX", "f", "F", "f*", "G", "g", "gs", "h",
            "i", "ID", "j", "J", "K", "k", "l", "m", "M", "MP", "n", "q", "Q", "re", "RG", "rg", "ri", "s", "S", "SC",
            "sc", "SCN", "scn", "sh", "T*", "Tc", "Td", "TD", "Tf", "Tj", "TJ", "TL", "Tm", "Tr", "Ts", "Tw", "Tz", "v",
            "w", "W", "W*", "y", "'", "\"");

    private final ConversionContext context;

    public PdfAContentStreamProcessor(ConversionContext context) {
        requireNotNullArg(context, "Conversion context cannot be null");
        this.context = context;
        addOperator(new Concatenate());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
        addOperator(new SetTextRenderingMode());
        //use the same map for stroking and not stroking
        Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> hitPatternsById = new HashMap<>();
        addOperator(tilingPatternSetStrokingColor(hitPatternsById));
        addOperator(tilingPatternSetNonStrokingColor(hitPatternsById));
    }

    final void addConversionOperator(PdfAContentStreamOperator op) {
        op.setConversionContext(context);
        addOperator(op);
    }

    final boolean addConversionOperatorIfAbsent(PdfAContentStreamOperator op) {
        op.setConversionContext(context);
        return addOperatorIfAbsent(op);
    }

    @Override
    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException {
        //this is valid for PDFA/1, make sure it's valid for other PDFA versions
        requireIOCondition(VALID_OPERATORS.contains(operator.getName()),
                "Operators not defined in the PDF Reference 1.4 are prohibited");
    }
}
