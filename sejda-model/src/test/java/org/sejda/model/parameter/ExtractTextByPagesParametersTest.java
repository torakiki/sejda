/*
 * Created on 26/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 * 
 */
public class ExtractTextByPagesParametersTest {
    private MultipleTaskOutput<?> output;
    private PdfSource<InputStream> input;

    @Before
    public void setUp() {
        output = mock(MultipleTaskOutput.class);
        InputStream stream = mock(InputStream.class);
        input = PdfStreamSource.newInstanceNoPassword(stream, "name");
    }

    @Test
    public void testEquals() {
        ExtractTextByPagesParameters eq1 = new ExtractTextByPagesParameters();
        ExtractTextByPagesParameters eq2 = new ExtractTextByPagesParameters();
        ExtractTextByPagesParameters eq3 = new ExtractTextByPagesParameters();
        ExtractTextByPagesParameters diff = new ExtractTextByPagesParameters();
        diff.setOverwrite(true);
        diff.setTextEncoding("UTF-8");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptySourceList() {
        ExtractTextByPagesParameters victim = new ExtractTextByPagesParameters();
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersInvalidRange() {
        ExtractTextByPagesParameters victim = new ExtractTextByPagesParameters();
        victim.setOutput(output);
        victim.setSource(input);
        victim.addPageRange(new PageRange(3, 2));
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersIntersectingRanges() {
        ExtractTextByPagesParameters victim = new ExtractTextByPagesParameters();
        victim.setOutput(output);
        victim.setSource(input);
        PageRange range1 = new PageRange(1, 20);
        PageRange range2 = new PageRange(10, 30);
        victim.addPageRange(range1);
        victim.addPageRange(range2);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void getPagesEmptyRange() {
        ExtractTextByPagesParameters victim = new ExtractTextByPagesParameters();
        assertEquals(10, victim.getPages(10).size());
    }

    @Test
    public void getPagesRange() {
        ExtractTextByPagesParameters victim = new ExtractTextByPagesParameters();
        victim.addPageRange(new PageRange(2, 5));
        assertEquals(4, victim.getPages(10).size());
    }
}
