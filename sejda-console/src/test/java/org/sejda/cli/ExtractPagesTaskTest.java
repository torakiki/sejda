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
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * Tests for the ExtractPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractPagesTaskTest extends AbstractTaskTest {

    public ExtractPagesTaskTest() {
        super(StandardTestableTask.EXTRACT_PAGES);
    }

    @Test
    public void predefinedPages_ALL_PAGES() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-m", "all").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 3, 4, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_ODD_PAGES() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-m", "odd").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 3, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_EVEN_PAGES() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-m", "even").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(2, 4), parameters.getPages(5));
    }

    @Test
    public void pageRange_combined() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-")
                .invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-m").without("-s")
                .assertConsoleOutputContains("Please specify at least one option that defines pages to be extracted");
    }

    @Test
    public void optimizedNo() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-z", "no").invokeSejdaConsole();
        assertEquals(OptimizationPolicy.NO, parameters.getOptimizationPolicy());
    }

    @Test
    public void optimizedYes() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-z", "yes").invokeSejdaConsole();
        assertEquals(OptimizationPolicy.YES, parameters.getOptimizationPolicy());
    }

    @Test
    public void discardOutline() {
        ExtractPagesParameters parameters = defaultCommandLine().withFlag("--discardOutline").invokeSejdaConsole();
        assertTrue(parameters.discardOutline());
    }

    @Test
    public void dontDiscardOutline() {
        ExtractPagesParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.discardOutline());
    }
}
