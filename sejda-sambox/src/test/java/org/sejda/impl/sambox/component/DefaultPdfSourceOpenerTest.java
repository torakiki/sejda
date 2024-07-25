/*
 * Created on 17 set 2015
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.impl.sambox.component;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskWrongPasswordException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.tests.TestUtils.customInputAsFileSource;

/**
 * @author Andrea Vacondio
 *
 */
public class DefaultPdfSourceOpenerTest {

    @Test
    public void openDocument() throws TaskIOException {
        assertNotNull(new DefaultPdfSourceOpener().open(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf"), "my source")));
    }

    @Test
    public void openDocumentWithPasswordAES128() throws TaskIOException {
        assertNotNull(new DefaultPdfSourceOpener().open(PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/encrypted_AES128_user_pwd.pdf"), "my source",
                "test")));
    }

    @Test
    public void openDocumentWithPasswordAES256() throws TaskIOException {
        assertNotNull(new DefaultPdfSourceOpener().open(PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/encrypted_AES256_user_pwd.pdf"), "my source",
                "test")));
    }

    @Test
    public void openDocumentWrongPassword() throws TaskIOException, IOException {
        try {
            File tmp = IOUtils.createTemporaryBufferWithName("dumbo.pdf");
            OutputStream out = new FileOutputStream(tmp);
            InputStream in = getClass().getClassLoader().getResourceAsStream("pdf/encrypted_AES128_user_pwd.pdf");

            try {
                org.apache.commons.io.IOUtils.copy(in, out);
            } finally {
                org.sejda.commons.util.IOUtils.closeQuietly(out);
                org.sejda.commons.util.IOUtils.closeQuietly(in);
            }

            new DefaultPdfSourceOpener().open(PdfFileSource.newInstanceWithPassword(tmp, "my source"));
            fail("Exception expected");
        } catch (TaskWrongPasswordException e) {
            assertEquals("Unable to open 'dumbo.pdf' due to a wrong password.", e.getMessage());
        }
    }

    @Test
    public void openDocumentError() {
        assertThrows(TaskIOException.class, () -> new DefaultPdfSourceOpener().open(
                PdfStreamSource.newInstanceNoPassword(
                        getClass().getClassLoader().getResourceAsStream("pdf/not_a_pdf.pdf"), "my source")));

    }

    @Test
    public void openDocumentTwice_streamSource_not_possible() throws TaskIOException, IOException {
        try (var is = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf")) {
            PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(is, "my source");
            try (PDDocumentHandler handler = new DefaultPdfSourceOpener().open(source)) {
                assertNotNull(handler.getUnderlyingPDDocument());
            }
            assertThrows(TaskIOException.class, () -> new DefaultPdfSourceOpener().open(source));
        }
    }

    @Test
    public void openDocumentTwice_fileSource() throws TaskIOException, IOException {
        PdfFileSource source = customInputAsFileSource("pdf/test_file.pdf");
        try(PDDocumentHandler handler = new DefaultPdfSourceOpener().open(source)) {
            assertNotNull(handler.getUnderlyingPDDocument());
        }

        try(PDDocumentHandler handler = new DefaultPdfSourceOpener().open(source)) {
            assertNotNull(handler.getUnderlyingPDDocument());
        }
    }
}
