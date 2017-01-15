/*
 * Created on Jul 1, 2011
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

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.parameter.SplitByOutlineLevelParameters;

/**
 * Tests for the SplitByBookmarksTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitByBookmarksTaskTest extends AbstractTaskTest {

    public SplitByBookmarksTaskTest() {
        super(StandardTestableTask.SPLIT_BY_BOOKMARKS);
    }

    @Test
    public void bookmarksLevel_Specified() {
        SplitByOutlineLevelParameters parameters = defaultCommandLine().with("-l", "3").invokeSejdaConsole();
        assertEquals(3, parameters.getLevelToSplitAt());
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-l").assertConsoleOutputContains("Option is mandatory: --bookmarkLevel");
    }

    @Test
    public void optimizedYes() {
        SplitByOutlineLevelParameters parameters = defaultCommandLine().with("-z", "yes").invokeSejdaConsole();
        assertEquals(OptimizationPolicy.YES, parameters.getOptimizationPolicy());
    }

    @Test
    public void matchingRegExp_Specified() {
        SplitByOutlineLevelParameters parameters = defaultCommandLine().with("--matchingRegEx", "[Chapter*]")
                .invokeSejdaConsole();
        assertEquals("[Chapter*]", parameters.getMatchingTitleRegEx());
    }

    @Test
    public void discardOutline() {
        SplitByOutlineLevelParameters parameters = defaultCommandLine().withFlag("--discardOutline")
                .invokeSejdaConsole();
        assertTrue(parameters.discardOutline());
    }

    @Test
    public void dontDiscardOutline() {
        SplitByOutlineLevelParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.discardOutline());
    }
}
