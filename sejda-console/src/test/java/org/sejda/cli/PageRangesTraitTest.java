/*
 * Created on 13/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;

import java.util.Arrays;
import java.util.Collection;

/**
 * For tasks that support page ranges, test various scenarios related to this trait
 *
 * @author Edi Weissmann
 * 
 */
public class PageRangesTraitTest extends AbstractTaskTraitTest {
    public PageRangesTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(new TestableTask[] { TestableTask.PDF_TO_JPEG , TestableTask.PDF_TO_MULTIPLE_TIFF , TestableTask.EXTRACT_PAGES });
    }

    @Test
    public void pageRanges() {
        PageRangeSelection parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-")
                .invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }
}
