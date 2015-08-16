/*
 * Created on Oct 25, 2013
 * Copyright 2013 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.Arrays;

import org.junit.Test;
import org.sejda.model.parameter.ExtractTextByPagesParameters;

/**
 * Tests for various traits of the ExtractTextByPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractTextByPagesTaskTest extends AbstractTaskTest {

    public ExtractTextByPagesTaskTest() {
        super(TestableTask.EXTRACT_TEXT_BY_PAGES);
    }

    @Test
    public void pages_Specified() {
        ExtractTextByPagesParameters parameters = defaultCommandLine().with("-s", "1-2,56,99,101").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 56, 99, 101), parameters.getPages(Integer.MAX_VALUE));
        assertEquals(5, parameters.getPages(Integer.MAX_VALUE).size());
    }

    @Test
    public void encoding_Specified() {
        ExtractTextByPagesParameters parameters = defaultCommandLine().with("-e", "ISO-8859-1").invokeSejdaConsole();
        assertEquals("ISO-8859-1", parameters.getTextEncoding());
    }

    @Test
    public void encoding_Default() {
        ExtractTextByPagesParameters parameters = defaultCommandLine().without("-e").invokeSejdaConsole();
        assertEquals("UTF-8", parameters.getTextEncoding());
    }

    @Test
    public void outputPrefix_Specified() {
        ExtractTextByPagesParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void outputPrefix_Default() {
        ExtractTextByPagesParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

}
