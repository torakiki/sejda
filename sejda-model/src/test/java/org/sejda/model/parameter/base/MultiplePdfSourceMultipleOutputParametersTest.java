/*
 * Created on 1 ott 2020
 * Copyright 2019 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
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
package org.sejda.model.parameter.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.output.SingleOrMultipleTaskOutput;

/**
 * @author Andrea Vacondio
 *
 */
public class MultiplePdfSourceMultipleOutputParametersTest {
    @Test
    public void testEquals() {
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        MultiplePdfSourceMultipleOutputParameters eq1 = new MultiplePdfSourceMultipleOutputParameters();
        eq1.setOutput(output);
        MultiplePdfSourceMultipleOutputParameters eq2 = new MultiplePdfSourceMultipleOutputParameters();
        eq2.setOutput(output);
        MultiplePdfSourceMultipleOutputParameters eq3 = new MultiplePdfSourceMultipleOutputParameters();
        eq3.setOutput(output);
        MultiplePdfSourceMultipleOutputParameters diff = new MultiplePdfSourceMultipleOutputParameters();
        diff.setOutput(mock(SingleOrMultipleTaskOutput.class));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void addSpecificFilenamesRetainsOrder() {
        MultiplePdfSourceMultipleOutputParameters victim = new MultiplePdfSourceMultipleOutputParameters();
        victim.addSpecificResultFilenames(Arrays.asList("one", "two", "three"));
        assertEquals("one.pdf", victim.getSpecificResultFilename(1));
        assertEquals("two.pdf", victim.getSpecificResultFilename(2));
        assertEquals("three.pdf", victim.getSpecificResultFilename(3));
    }

    @Test
    public void getSpecificResultFilename() {
        MultiplePdfSourceMultipleOutputParameters victim = new MultiplePdfSourceMultipleOutputParameters();
        victim.addSpecificResultFilenames(Arrays.asList("one", "two", "three.json"));
        assertEquals("one.txt", victim.getSpecificResultFilename(1, ".txt"));
        assertEquals("two.pdf", victim.getSpecificResultFilename(2));
        assertEquals("three.json", victim.getSpecificResultFilename(3, ""));
        assertNull(victim.getSpecificResultFilename(10));
    }
}
