/*
 * Created on 20 ott 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Dimension;
import java.io.InputStream;
import java.util.Set;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.FileSource;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.StreamSource;
import org.sejda.model.output.SingleOrMultipleTaskOutput;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.watermark.Location;

/**
 * @author Andrea Vacondio
 *
 */
public class WatermarkParametersTest {
    @Test
    public void testEquals() {
        FileSource source = mock(FileSource.class);
        WatermarkParameters eq1 = new WatermarkParameters(source);
        eq1.setOpacity(50);
        WatermarkParameters eq2 = new WatermarkParameters(source);
        eq2.setOpacity(50);
        WatermarkParameters eq3 = new WatermarkParameters(source);
        eq3.setOpacity(50);
        WatermarkParameters diff = new WatermarkParameters(source);
        diff.setOpacity(50);
        diff.setLocation(Location.OVER);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void getPages() {
        WatermarkParameters victim = new WatermarkParameters(StreamSource.newInstance(mock(InputStream.class), "name"));
        victim.addPageRange(new PageRange(5, 8));
        victim.addPageRange(new PageRange(3, 3));
        victim.addPageRange(new PageRange(10));
        Set<Integer> pages = victim.getPages(11);
        assertThat(pages, contains(5, 6, 7, 8, 3, 10, 11));
        assertThat(pages, not(contains(1, 2, 4, 9)));
    }

    @Test
    public void invalidParametersEmptyWatermark() {
        WatermarkParameters victim = new WatermarkParameters(null);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(mock(InputStream.class), "name");
        victim.addSource(input);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void invalidParametersOpacity() {
        WatermarkParameters victim = new WatermarkParameters(StreamSource.newInstance(mock(InputStream.class), "name"));
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(mock(InputStream.class), "name");
        victim.addSource(input);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        victim.setOpacity(-1);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void invalidParametersImageSize() {
        WatermarkParameters victim = new WatermarkParameters(StreamSource.newInstance(mock(InputStream.class), "name"));
        victim.setDimension(new Dimension(-300, 300));
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(mock(InputStream.class), "name");
        victim.addSource(input);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        TestUtils.assertInvalidParameters(victim);
    }

}
