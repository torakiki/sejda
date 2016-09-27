/*
 * Created on 09/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Set;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.RectangularBox;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 * 
 */
public class CropParametersTest {

    @Test
    public void testEquals() {
        CropParameters eq1 = new CropParameters();
        eq1.addCropArea(RectangularBox.newInstance(0, 1, 10, 9));
        CropParameters eq2 = new CropParameters();
        eq2.addCropArea(RectangularBox.newInstance(0, 1, 10, 9));
        CropParameters eq3 = new CropParameters();
        eq3.addCropArea(RectangularBox.newInstance(0, 1, 10, 9));
        CropParameters diff = new CropParameters();
        diff.addCropArea(RectangularBox.newInstance(1, 1, 10, 9));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testAdd() {
        CropParameters victim = new CropParameters();
        RectangularBox area = RectangularBox.newInstance(0, 1, 10, 9);
        victim.addCropArea(area);
        Set<RectangularBox> areas = victim.getCropAreas();
        assertEquals(1, areas.size());
        assertTrue(areas.contains(area));
    }

    @Test
    public void testExcludedPages() {
        CropParameters victim = new CropParameters();
        RectangularBox area = RectangularBox.newInstance(0, 1, 10, 9);
        victim.addCropArea(area);
        victim.addExcludedPage(2);
        victim.addExcludedPageRange(new PageRange(5, 8));
        Set<Integer> excluded = victim.getExcludedPages(6);
        assertEquals(3, excluded.size());
        assertTrue(excluded.contains(2));
        assertTrue(excluded.contains(5));
        assertTrue(excluded.contains(6));
    }

    @Test
    public void testInvalidParameters() {
        CropParameters victim = new CropParameters();
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
