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

import java.util.Arrays;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.input.FileIndexAndPage;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.rotation.Rotation;

public class CombineReorderTaskTest extends AbstractTaskTest {

    public CombineReorderTaskTest() {
        super(StandardTestableTask.COMBINE_REORDER);
    }

    @Test
    public void testPages_Specified() {
        CombineReorderParameters parameters = defaultCommandLine().with("-n", "0:1 1:1 0:2 1:3")
                .invokeSejdaConsole();
        assertEquals(Arrays.asList(new FileIndexAndPage(0, 1), new FileIndexAndPage(1, 1), new FileIndexAndPage(0, 2), new FileIndexAndPage(1, 3)),
                parameters.getPages());
    }

    @Test
    public void testPagesWithRotation_Specified() {
        CombineReorderParameters parameters = defaultCommandLine().with("-n", "0:1 1:1:270 0:2:180 1:3:90")
                .invokeSejdaConsole();

        assertEquals(Arrays.asList(new FileIndexAndPage(0, 1), new FileIndexAndPage(1, 1, Rotation.DEGREES_270), new FileIndexAndPage(0, 2, Rotation.DEGREES_180), new FileIndexAndPage(1, 3, Rotation.DEGREES_90)),
                parameters.getPages());
    }
}
