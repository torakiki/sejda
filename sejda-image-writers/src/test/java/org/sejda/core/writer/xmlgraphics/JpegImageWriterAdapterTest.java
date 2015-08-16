/*
 * Created on 01/mar/2013
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
package org.sejda.core.writer.xmlgraphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.ImageTestUtils;
import org.sejda.core.writer.xmlgraphics.JpegImageWriterAdapter.JpegImageWriterAdapterBuilder;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * @author Andrea Vacondio
 * 
 */
public class JpegImageWriterAdapterTest {
    private JpegImageWriterAdapter victim;

    @Before
    public void setUp() {
        victim = new JpegImageWriterAdapterBuilder().build();
    }

    @Test(expected = TaskIOException.class)
    public void writeNotOpened() throws TaskIOException {
        PdfToJpegParameters params = mock(PdfToJpegParameters.class);
        RenderedImage image = mock(RenderedImage.class);
        victim.write(image, params);
    }

    @Test
    public void supportMultiImage() {
        assertFalse(victim.supportMultiImage());
    }

    @Test
    public void write() throws IOException, TaskIOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("image/test.jpg");
        File destination = File.createTempFile("test", ".tmp");
        destination.deleteOnExit();
        PdfToJpegParameters params = new PdfToJpegParameters();
        RenderedImage image = ImageTestUtils.loadImage(stream, "test.jpg");
        victim.openWriteDestination(destination, params);
        victim.write(image, params);
        victim.closeDestination();
        victim.close();
        RenderedImage result = ImageTestUtils.loadImage(destination);
        assertTrue(result.getHeight() > 0);
        assertTrue(result.getWidth() > 0);
    }
}
