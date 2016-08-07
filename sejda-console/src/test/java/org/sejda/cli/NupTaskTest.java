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
import org.sejda.model.nup.PageOrder;
import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.NupParameters;
import org.sejda.model.parameter.OptimizeParameters;

import static org.junit.Assert.assertEquals;

public class NupTaskTest extends AbstractTaskTest {

    public NupTaskTest() {
        super(TestableTask.NUP);
    }

    @Test
    public void testN() {
        NupParameters parameters = defaultCommandLine().with("-n", "8").invokeSejdaConsole();
        assertEquals(8, parameters.getN());
    }

    @Test
    public void testPageOrder() {
        NupParameters parameters = defaultCommandLine().withFlag("--verticalOrdering").invokeSejdaConsole();
        assertEquals(PageOrder.VERTICAL, parameters.getPageOrder());
    }

    @Test
    public void testDefaultPageOrder() {
        NupParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(PageOrder.HORIZONTAL, parameters.getPageOrder());
    }

    @Test
    public void testDefaultN() {
        NupParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(4, parameters.getN());
    }
}
