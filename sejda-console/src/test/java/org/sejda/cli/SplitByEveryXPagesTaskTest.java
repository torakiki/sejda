/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import org.sejda.model.parameter.SplitByEveryXPagesParameters;

/**
 * Tests for the SplitByEveryXPageTask command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesTaskTest extends AbstractTaskTest {

    public SplitByEveryXPagesTaskTest() {
        super(StandardTestableTask.SPLIT_BY_EVERY);
    }

    @Test
    public void pages_Specified() {
        SplitByEveryXPagesParameters parameters = defaultCommandLine().with("-n", "5").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(5, 10, 15, 20, 25), parameters.getPages(27));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-n").assertConsoleOutputContains("Option is mandatory: --pages");
    }

    @Test
    public void discardOutline() {
        SplitByEveryXPagesParameters parameters = defaultCommandLine().withFlag("--discardOutline")
                .invokeSejdaConsole();
        assertTrue(parameters.discardOutline());
    }

    @Test
    public void dontDiscardOutline() {
        SplitByEveryXPagesParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.discardOutline());
    }
}
