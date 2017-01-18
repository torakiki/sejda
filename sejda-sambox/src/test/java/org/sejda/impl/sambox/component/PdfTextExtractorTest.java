/*
 * Created on 25/ago/2011
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
package org.sejda.impl.sambox.component;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.internal.matchers.StartsWith;
import org.sejda.model.exception.TaskException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfTextExtractorTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void skipNullPage() throws IOException, TaskException {
        try (PdfTextExtractor victim = new PdfTextExtractor("UTF-8", folder.newFile())) {
            victim.extract((PDPage) null);
        }
    }

    @Test
    public void skipNoContentsPage() throws IOException, TaskException {
        PDPage page = mock(PDPage.class);
        when(page.hasContents()).thenReturn(Boolean.FALSE);
        try (PdfTextExtractor victim = new PdfTextExtractor("UTF-8", folder.newFile())) {
            victim.extract(page);
        }
    }

    @Test
    public void skipNullDocument() throws IOException, TaskException {
        try (PdfTextExtractor victim = new PdfTextExtractor("UTF-8", folder.newFile())) {
            victim.extract((PDDocument) null);
        }
    }

    @Test
    public void testNullFileExtract() throws TaskException {
        expected.expectMessage(new StartsWith("Cannot write extracted text"));
        new PdfTextExtractor("UTF-8", null);
    }

    @Test
    public void testNotFileExtract() throws TaskException {
        File file = mock(File.class);
        expected.expectMessage(new StartsWith("Cannot write extracted text"));
        when(file.isFile()).thenReturn(Boolean.FALSE);
        new PdfTextExtractor("UTF-8", file);
    }

    @Test
    public void testCannotWriteFileExtract() throws TaskException {
        File file = mock(File.class);
        expected.expectMessage(new StartsWith("Cannot write extracted text"));
        when(file.isFile()).thenReturn(Boolean.TRUE);
        when(file.canWrite()).thenReturn(Boolean.FALSE);
        new PdfTextExtractor("UTF-8", file);
    }
}
