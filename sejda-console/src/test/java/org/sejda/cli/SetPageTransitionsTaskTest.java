/*
 * Created on Sep 20, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.SetPagesTransitionParameters;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransition;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransitionStyle;

/**
 * Tests for SetPageTransitions task cli
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetPageTransitionsTaskTest extends AbstractTaskTest {

    public SetPageTransitionsTaskTest() {
        super(TestableTask.SET_PAGE_TRANSITIONS);
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
