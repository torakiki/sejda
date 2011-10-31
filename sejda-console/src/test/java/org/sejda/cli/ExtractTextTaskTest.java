/*
 * Created on Sep 13, 2011
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
