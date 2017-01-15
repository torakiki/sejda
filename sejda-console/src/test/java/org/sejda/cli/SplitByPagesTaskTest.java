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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.SplitByPagesParameters;

/**
 * Tests for the SplitByPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitByPagesTaskTest extends AbstractTaskTest {

    public SplitByPagesTaskTest() {
        super(StandardTestableTask.SPLIT_BY_PAGES);
    }

    @Test
    public void pages_Specified() {
        SplitByPagesParameters parameters = defaultCommandLine().with("-n", "1 2 56 99 101").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 56, 99, 101), parameters.getPages(Integer.MAX_VALUE));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-n").assertConsoleOutputContains("Option is mandatory: --pageNumbers");
    }

    @Test
    public void discardOutline() {
        SplitByPagesParameters parameters = defaultCommandLine().withFlag("--discardOutline").invokeSejdaConsole();
        assertTrue(parameters.discardOutline());
    }

    @Test
    public void dontDiscardOutline() {
        SplitByPagesParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.discardOutline());
    }
}
