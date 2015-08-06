/*
 * Created on Sep 13, 2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.model.parameter.ExtractTextParameters;

/**
 * Tests for various traits of the ExtractTextTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractTextTaskTest extends AbstractTaskTest {

    public ExtractTextTaskTest() {
        super(TestableTask.EXTRACT_TEXT);
    }

    @Test
    public void encoding_Specified() {
        ExtractTextParameters parameters = defaultCommandLine().with("-e", "ISO-8859-1").invokeSejdaConsole();
        assertEquals("ISO-8859-1", parameters.getTextEncoding());
    }

    @Test
    public void encoding_Default() {
        ExtractTextParameters parameters = defaultCommandLine().without("-e").invokeSejdaConsole();
        assertEquals("UTF-8", parameters.getTextEncoding());
    }

    @Test
    public void outputPrefix_Specified() {
        ExtractTextParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void outputPrefix_Default() {
        ExtractTextParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

}
