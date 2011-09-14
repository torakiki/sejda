/*
 * Created on Sep 12, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.Arrays;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.ExtractPagesParameters;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * Tests for the ExtractPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ExtractPagesTaskTest extends AbstractTaskTest {

    public ExtractPagesTaskTest() {
        super(TestableTask.EXTRACT_PAGES);
    }

    @Test
    public void predefinedPages_ALL_PAGES() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-p", "ALL_PAGES").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 3, 4, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_ODD_PAGES() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-p", "ODD_PAGES").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 3, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_EVEN_PAGES() {
        ExtractPagesParameters parameters = defaultCommandLine().with("-p", "EVEN_PAGES").invokeSejdaConsole();
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
        defaultCommandLine().without("-p").without("-s")
                .assertConsoleOutputContains("Please specify at least one option that defines pages to be extracted");
    }
}
