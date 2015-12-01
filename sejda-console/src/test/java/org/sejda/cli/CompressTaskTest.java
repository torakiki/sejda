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

import org.junit.Test;
import org.sejda.model.parameter.OptimizeParameters;

import static org.junit.Assert.assertEquals;

public class CompressTaskTest extends AbstractTaskTest {

    public CompressTaskTest() {
        super(TestableTask.COMPRESS);
    }

    @Test
    public void testImageDpi() {
        OptimizeParameters parameters = defaultCommandLine().with("--imageDpi", "100")
                .invokeSejdaConsole();
        assertEquals(parameters.getImageDpi(), 100);
    }

    @Test
    public void testImageMax() {
        OptimizeParameters parameters = defaultCommandLine().with("--imageMaxWidthOrHeight", "110")
                .invokeSejdaConsole();
        assertEquals(parameters.getImageMaxWidthOrHeight(), 110);
    }

    @Test
    public void testImageQuality() {
        OptimizeParameters parameters = defaultCommandLine().with("--imageQuality", "0.22")
                .invokeSejdaConsole();
        assertEquals(parameters.getImageQuality(), 0.22f, 0.01);
    }
}
