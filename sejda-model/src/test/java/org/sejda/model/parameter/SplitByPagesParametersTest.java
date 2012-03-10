/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class SplitByPagesParametersTest {

    @Test
    public void testEquals() {
        SplitByPagesParameters eq1 = new SplitByPagesParameters();
        eq1.addPage(1);
        SplitByPagesParameters eq2 = new SplitByPagesParameters();
        eq2.addPage(1);
        SplitByPagesParameters eq3 = new SplitByPagesParameters();
        eq3.addPage(1);
        SplitByPagesParameters diff = new SplitByPagesParameters();
        diff.addPage(2);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void getPages() {
        SplitByPagesParameters victim = new SplitByPagesParameters();
        victim.addPage(1);
        victim.addPage(10);
        assertEquals(1, victim.getPages(5).size());
        assertEquals(2, victim.getPages(15).size());
    }

    @Test
    public void testInvalidParameters() {
        SplitByPagesParameters victim = new SplitByPagesParameters();
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
