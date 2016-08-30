/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.junit.Test;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.split.SplitDownTheMiddleMode;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

/**
 * Tests for the SplitDownTheMiddleTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitDownTheMiddleTaskTest extends AbstractTaskTest {

    public SplitDownTheMiddleTaskTest() {
        super(TestableTask.SPLIT_DOWN_THE_MIDDLE);
    }

    @Test
    public void repaginationSpecified() {
        SplitDownTheMiddleParameters parameters = defaultCommandLine().with("--repagination", "last-first").invokeSejdaConsole();
        assertEquals(Repagination.LAST_FIRST, parameters.getRepagination());
    }

    @Test
    public void ratioSpecified() {
        SplitDownTheMiddleParameters parameters = defaultCommandLine().with("--ratio", "0.25").invokeSejdaConsole();
        assertEquals(0.25d, parameters.getRatio(), 0.0);
    }

    @Test
    public void modeSpecified() {
        SplitDownTheMiddleParameters parameters = defaultCommandLine().with("--mode", "vertical").invokeSejdaConsole();
        assertEquals(SplitDownTheMiddleMode.VERTICAL, parameters.getMode());
    }

    @Test
    public void excludedPagesSpecified() {
        SplitDownTheMiddleParameters parameters = defaultCommandLine().with("--excludedPages", "1,3-5").invokeSejdaConsole();
        assertThat(parameters.getExcludedPagesSelection(), hasItems(new PageRange(1, 1), new PageRange(3, 5)));
    }

    @Test
    public void defaults() {
        SplitDownTheMiddleParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(Repagination.NONE, parameters.getRepagination());
        assertEquals(1, parameters.getRatio(), 0.0d);
        assertEquals(SplitDownTheMiddleMode.AUTO, parameters.getMode());
    }
}
