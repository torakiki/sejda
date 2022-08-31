/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox.component;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.graphics.color.PDDeviceRGB;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.pdmodel.graphics.image.UnsupportedImageFormatException;
import org.sejda.sambox.util.filetypedetector.FileType;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sejda.tests.tasks.BaseTaskTest.customNonPdfInput;
import static org.sejda.tests.tasks.BaseTaskTest.customNonPdfInputAsFileSource;
import static org.sejda.tests.TestUtils.encryptedAtRest;

public class PageImageWriterTest {

    @Test
    public void testJpeg() throws TaskIOException, IOException {

        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/large.jpg"));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));
        assertThat(result.getHeight(), is(3840));
        assertThat(result.getWidth(), is(5760));

        assertTrue(IOUtils.contentEquals(result.getCOSObject().getFilteredStream(),
                customNonPdfInput("/image/large.jpg").getSource()), "Original bytes should be used");
    }

    @Test
    public void testHeif_unsupported() throws TaskIOException {
        Exception ex = assertThrows(TaskIOException.class, () -> {
            PageImageWriter.toPDXImageObject(customNonPdfInput("/image/sample_heic.jpg"));
        });

        Throwable cause = ex.getCause();
        assertEquals(cause.getClass(), UnsupportedImageFormatException.class);
        UnsupportedImageFormatException uife = (UnsupportedImageFormatException) cause;

        assertEquals(uife.getFileType(), FileType.HEIF);
        assertEquals(uife.getFilename(), "sample_heic.jpg");
    }

    @Test
    public void testPng() throws TaskIOException, IOException {

        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/draft.png"));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));
        assertThat(result.getHeight(), is(103));
        assertThat(result.getWidth(), is(248));
    }

    @Test
    public void testTiffWithAlphaToPDXImageObject() throws TaskIOException {
        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/draft.tiff"));
        assertThat(result.getHeight(), is(103));
    }

    @Test
    public void test_CMYK_jpeg() throws TaskIOException, IOException {
        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/cmyk.jpg"));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));
        assertThat(result.getHeight(), is(560));
        assertThat(result.getWidth(), is(1400));

        assertFalse(IOUtils.contentEquals(result.getCOSObject().getFilteredStream(),
                        customNonPdfInput("/image/cmyk.jpg").getSource()),
                "Original bytes should not be used; the image should be converted from CMYK to RGB");
    }

    @Test
    public void test_CMYK_jpeg_wrong_extension() throws TaskIOException, IOException {
        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/cmyk.jpg", "cmyk.png"));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));
        assertThat(result.getHeight(), is(560));
        assertThat(result.getWidth(), is(1400));

        assertFalse(IOUtils.contentEquals(result.getCOSObject().getFilteredStream(),
                        customNonPdfInput("/image/cmyk.jpg").getSource()),
                "Original bytes should not be used; the image should be converted from CMYK to RGB");
    }

    @Test
    public void test_Gray_ICC_png() throws TaskIOException, IOException {
        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/icc_profile_gray.png"));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));

        assertFalse(IOUtils.contentEquals(result.getCOSObject().getFilteredStream(),
                        customNonPdfInput("/image/icc_profile_gray.png").getSource()),
                "Original bytes should not be used; the image should be converted from ICC Gray to RGB");
    }

    @Test
    public void encryptedAtRestTest_stream() throws TaskIOException, IOException {

        PDImageXObject result = PageImageWriter.toPDXImageObject(
                encryptedAtRest(customNonPdfInput("/image/large.jpg")));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));
        assertThat(result.getHeight(), is(3840));

        assertTrue(IOUtils.contentEquals(result.getCOSObject().getFilteredStream(),
                customNonPdfInput("/image/large.jpg").getSource()), "Decrypted bytes should be used");
    }

    @Test
    public void encryptedAtRestTest_file() throws TaskIOException, IOException {

        PDImageXObject result = PageImageWriter.toPDXImageObject(
                encryptedAtRest(customNonPdfInputAsFileSource("/image/draft.png")));
        assertThat(result.getColorSpace(), is(PDDeviceRGB.INSTANCE));
        assertThat(result.getHeight(), is(103));
    }

    @Test
    public void testExifRotated() throws TaskIOException {

        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("/image/with_exif_orientation.JPG"));
        assertThat(result.getHeight(), is(3264));
        assertThat(result.getWidth(), is(2448));
    }
}
