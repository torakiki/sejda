/*
 * Created on 27 set 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.optimization.Optimization;
import org.sejda.model.output.MultipleTaskOutput;

/**
 * @author Andrea Vacondio
 *
 */
public class OptimizeParametersTest {

    @Test
    public void testEquals() {
        OptimizeParameters eq1 = new OptimizeParameters();
        eq1.addOptimization(Optimization.DISCARD_ALTERNATE_IMAGES);
        OptimizeParameters eq2 = new OptimizeParameters();
        eq2.addOptimization(Optimization.DISCARD_ALTERNATE_IMAGES);
        OptimizeParameters eq3 = new OptimizeParameters();
        eq3.addOptimization(Optimization.DISCARD_ALTERNATE_IMAGES);
        OptimizeParameters diff = new OptimizeParameters();
        diff.addOptimization(Optimization.DISCARD_ALTERNATE_IMAGES);
        diff.addOptimization(Optimization.DISCARD_OUTLINE);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void invalidParametersEmptyOptimizations() {
        OptimizeParameters victim = new OptimizeParameters();
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        TestUtils.assertInvalidParameters(victim);
    }
}
