/*
 * Created on 12/ago/2011
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
package org.sejda.core.manipulation.model.input;

import org.junit.Test;
import org.mockito.Mockito;
import org.sejda.core.TestUtils;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfMergeInputTest {

    @Test
    public void testEqual() {
        PdfSource source = Mockito.mock(PdfSource.class);
        PageRange range = new PageRange(10);
        PdfMergeInput eq1 = new PdfMergeInput(source);
        eq1.addPageRange(range);
        PdfMergeInput eq2 = new PdfMergeInput(source);
        eq2.addPageRange(range);
        PdfMergeInput eq3 = new PdfMergeInput(source);
        eq3.addPageRange(range);
        PdfMergeInput diff = new PdfMergeInput(source);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
