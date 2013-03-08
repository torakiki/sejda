/*
 * Created on 01/mar/2013
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
