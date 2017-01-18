/*
 * Created on Sep 20, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

/**
 * Tests for SetPageTransitions task cli
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetPageTransitionsTaskTest extends AbstractTaskTest {

    public SetPageTransitionsTaskTest() {
        super(StandardTestableTask.SET_PAGE_TRANSITIONS);
    }

    @Test
    public void unrecognizedTransitionType() {
        defaultCommandLine().with("--transitions", "INVALID:6:9:55").assertConsoleOutputContains(
                "Invalid value 'INVALID' for transition type");
    }

    @Test
    public void unrecognizedTransitionDuration() {
        defaultCommandLine().with("--transitions", "DISSOLVE:abc:9:55").assertConsoleOutputContains(
                "Unrecognized transition duration in seconds: 'abc'");
    }

    @Test
    public void unrecognizedPageDisplayDuration() {
        defaultCommandLine().with("--transitions", "DISSOLVE:6:abc:55").assertConsoleOutputContains(
                "Unrecognized page display duration in seconds: 'abc'");
    }

    @Test
    public void unrecognizedPageNumber() {
        defaultCommandLine().with("--transitions", "DISSOLVE:6:9:abc").assertConsoleOutputContains(
                "Unrecognized page number: 'abc'");
    }

    @Test
    public void missingPageNumberInTransitions() {
        defaultCommandLine().with("--transitions", "DISSOLVE:6:9").assertConsoleOutputContains(
                "Expected format is: 'transitionType:transitionDurationInSec:pageDisplayDurationInSec:pageNumber");
    }

    @Test
    public void unexpectedFormatTransition() {
        defaultCommandLine().with("--defaultTransition", "DISSOLVE:6").assertConsoleOutputContains(
                "Expected format is: 'transitionType:transitionDurationInSec:pageDisplayDurationInSec'");
    }

    @Test
    public void defaultTransition() {
        SetPagesTransitionParameters parameters = defaultCommandLine().with("--defaultTransition", "DISSOLVE:6:9")
                .invokeSejdaConsole();
        assertThat(parameters.getDefaultTransition(),
                equalTo(PdfPageTransition.newInstance(PdfPageTransitionStyle.DISSOLVE, 6, 9)));
    }

    @Test
    public void transitions() {
        SetPagesTransitionParameters parameters = defaultCommandLine().with("--transitions",
                "DISSOLVE:6:9:1 GLITTER_DIAGONAL:99:1:10").invokeSejdaConsole();

        assertThat(parameters.getTransitions().keySet(), hasItems(1, 10));

        assertThat(parameters.getTransitions().get(1),
                equalTo(PdfPageTransition.newInstance(PdfPageTransitionStyle.DISSOLVE, 6, 9)));

        assertThat(parameters.getTransitions().get(10),
                equalTo(PdfPageTransition.newInstance(PdfPageTransitionStyle.GLITTER_DIAGONAL, 99, 1)));

    }
}
