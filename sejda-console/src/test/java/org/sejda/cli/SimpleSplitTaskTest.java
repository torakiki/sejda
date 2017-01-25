/*
 * Created on Sep 12, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.parameter.SimpleSplitParameters;

/**
 * Tests for the SimpleSplitTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SimpleSplitTaskTest extends AbstractTaskTest {

    public SimpleSplitTaskTest() {
        super(StandardTestableTask.SIMPLE_SPLIT);
    }

    @Test
    public void predefinedPages_ALL_PAGES() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-s", "all").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 3, 4, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_ODD_PAGES() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-s", "odd").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 3, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_EVEN_PAGES() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-s", "even").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(2, 4), parameters.getPages(5));
    }

    @Test
    public void optimizedNo() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-z", "no").invokeSejdaConsole();
        assertEquals(OptimizationPolicy.NO, parameters.getOptimizationPolicy());
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-s").assertConsoleOutputContains("Option is mandatory: --predefinedPages");
    }

    @Test
    public void discardOutline() {
        SimpleSplitParameters parameters = defaultCommandLine().withFlag("--discardOutline").invokeSejdaConsole();
        assertTrue(parameters.discardOutline());
    }

    @Test
    public void dontDiscardOutline() {
        SimpleSplitParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.discardOutline());
    }
}
