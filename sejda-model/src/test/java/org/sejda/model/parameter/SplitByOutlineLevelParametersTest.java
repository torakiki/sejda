/*
 * Created on 06/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.SingleOrMultipleTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class SplitByOutlineLevelParametersTest {

    @Test
    public void testEquals() {
        SplitByOutlineLevelParameters eq1 = new SplitByOutlineLevelParameters(10);
        SplitByOutlineLevelParameters eq2 = new SplitByOutlineLevelParameters(10);
        SplitByOutlineLevelParameters eq3 = new SplitByOutlineLevelParameters(10);
        SplitByOutlineLevelParameters diff = new SplitByOutlineLevelParameters(1);
        diff.setOutputPrefix("prefix");
        diff.setMatchingTitleRegEx("string");
        diff.setOptimizationPolicy(OptimizationPolicy.AUTO);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParameters() {
        SplitByOutlineLevelParameters victim = new SplitByOutlineLevelParameters(-1);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
