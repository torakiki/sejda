/*
 * Created on 26/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
