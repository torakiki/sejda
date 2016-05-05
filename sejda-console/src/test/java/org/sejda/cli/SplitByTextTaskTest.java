/*
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.parameter.SplitByTextContentParameters;

public class SplitByTextTaskTest extends AbstractTaskTest {

    public SplitByTextTaskTest() {
        super(TestableTask.SPLIT_BY_TEXT);
    }

    @Test
    public void basicParams() {
        SplitByTextContentParameters parameters = defaultCommandLine().with("--top", "10").with("--left", "11")
                .with("--width", "12").with("--height", "13").with("--startsWith", "Fax:").invokeSejdaConsole();
        assertEquals(new TopLeftRectangularBox(10, 11, 12, 13), parameters.getTextArea());
        assertEquals("Fax:", parameters.getStartsWith());
    }

    @Test
    public void optimizedYes() {
        SplitByTextContentParameters parameters = defaultCommandLine().with("-z", "yes").invokeSejdaConsole();
        assertEquals(OptimizationPolicy.YES, parameters.getOptimizationPolicy());
    }

    @Test
    public void discardOutline() {
        SplitByTextContentParameters parameters = defaultCommandLine().withFlag("--discardOutline")
                .invokeSejdaConsole();
        assertTrue(parameters.discardOutline());
    }

    @Test
    public void dontDiscardOutline() {
        SplitByTextContentParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.discardOutline());
    }
}
