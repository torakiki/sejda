/*
 * Created on 26/ago/2011
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
package org.sejda.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Collections;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * @author Andrea Vacondio
 * 
 */
public class ExtractPagesParametersTest {
    @Test
    public void testEquals() {
        ExtractPagesParameters eq1 = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        ExtractPagesParameters eq2 = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        ExtractPagesParameters eq3 = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        ExtractPagesParameters diff = new ExtractPagesParameters(Collections.singletonList(new PageRange(12)));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testGetPageSelection() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        assertTrue(victim.getPageSelection().isEmpty());
        ExtractPagesParameters victim2 = new ExtractPagesParameters(Collections.singletonList(new PageRange(12)));
        assertFalse(victim2.getPageSelection().isEmpty());
    }

    @Test
    public void getPages() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        assertEquals(5, victim.getPages(10).size());
        ExtractPagesParameters victim2 = new ExtractPagesParameters(Collections.singletonList(new PageRange(12)));
        assertEquals(4, victim2.getPages(15).size());
    }

    @Test
    public void testInvalidParameters() {
        ExtractPagesParameters victim = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        TaskOutput output = mock(TaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
