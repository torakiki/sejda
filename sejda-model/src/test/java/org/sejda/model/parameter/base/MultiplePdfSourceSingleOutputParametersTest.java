/*
 * Created on 30/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter.base;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.output.SingleTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class MultiplePdfSourceSingleOutputParametersTest {

    @Test
    public void testEquals() {
        SingleTaskOutput output = mock(SingleTaskOutput.class);
        MockMultiplePdfSourceSingleOutputParameters eq1 = new MockMultiplePdfSourceSingleOutputParameters();
        eq1.setOutput(output);
        MockMultiplePdfSourceSingleOutputParameters eq2 = new MockMultiplePdfSourceSingleOutputParameters();
        eq2.setOutput(output);
        MockMultiplePdfSourceSingleOutputParameters eq3 = new MockMultiplePdfSourceSingleOutputParameters();
        eq3.setOutput(output);
        MockMultiplePdfSourceSingleOutputParameters diff = new MockMultiplePdfSourceSingleOutputParameters();
        diff.setOutput(mock(SingleTaskOutput.class));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    private class MockMultiplePdfSourceSingleOutputParameters extends MultiplePdfSourceSingleOutputParameters {
        // nothing
    }
}
