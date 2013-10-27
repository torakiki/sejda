/*
 * Created on Oct 25, 2013
 * Copyright 2013 by Eduard Weissmann (edi.weissmann@gmail.com).
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
        ExtractTextByPagesParameters parameters = defaultCommandLine().with("-n", "1 2 56 99 101").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 56, 99, 101), parameters.getPages(Integer.MAX_VALUE));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-n").assertConsoleOutputContains("Option is mandatory: --pageNumbers");
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
