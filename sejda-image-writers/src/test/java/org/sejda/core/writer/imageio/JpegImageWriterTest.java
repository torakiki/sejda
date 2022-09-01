/*
 * Created on 02 feb 2017
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
package org.sejda.core.writer.imageio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.parameter.image.PdfToJpegParameters;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.sejda.tests.TestUtils.getEncryptionAtRestPolicy;

/**
 * @author Andrea Vacondio
 *
 */
public class JpegImageWriterTest {
    private JpegImageWriter victim;

    @BeforeEach
    public void setUp() {
        victim = new JpegImageWriter();
    }

    @Test
    public void writeNotOpened() {
        PdfToJpegParameters params = mock(PdfToJpegParameters.class);
        RenderedImage image = mock(RenderedImage.class);
        assertThrows(TaskException.class, () -> victim.write(image, params));
    }

    @Test
    public void supportMultiImage() {
        assertFalse(victim.supportMultiImage());
    }

    @Test
    public void write() throws IOException, TaskIOException {
        InputStream stream = getClass().getResourceAsStream("/image/test.jpg");
        File destination = File.createTempFile("test", ".tmp");
        destination.deleteOnExit();
        PdfToJpegParameters params = new PdfToJpegParameters(ImageColorType.GRAY_SCALE);
        params.setOutput(new FileOrDirectoryTaskOutput(destination));

        RenderedImage image = ImageIO.read(stream);
        assertNotNull(image);

        victim.openDestination(destination, params);
        victim.write(image, params);
        victim.closeDestination();
        victim.close();

        RenderedImage result = ImageIO.read(destination);
        assertTrue(result.getHeight() > 0);
        assertTrue(result.getWidth() > 0);
    }

    @Test
    public void writeEncrypted() throws IOException, TaskIOException {
        InputStream stream = getClass().getResourceAsStream("/image/test.jpg");
        File destination = File.createTempFile("test", ".jpg");
        destination.deleteOnExit();

        PdfToJpegParameters params = new PdfToJpegParameters(ImageColorType.GRAY_SCALE);
        params.setOutput(new FileOrDirectoryTaskOutput(destination));
        params.getOutput().setEncryptionAtRestPolicy(getEncryptionAtRestPolicy());

        RenderedImage image = ImageIO.read(stream);
        assertNotNull(image);

        victim.openDestination(destination, params);
        victim.write(image, params);
        victim.closeDestination();
        victim.close();

        RenderedImage result = ImageIO.read(getEncryptionAtRestPolicy().decrypt(new FileInputStream(destination)));
        assertTrue(result.getHeight() > 0);
        assertTrue(result.getWidth() > 0);
    }
}
