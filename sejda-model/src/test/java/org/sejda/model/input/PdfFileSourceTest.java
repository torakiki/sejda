/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.input;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskIOException;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sejda.tests.TestUtils.encryptedAtRest;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfFileSourceTest {

    @Test
    public void testNullFile() {
        assertThrows(IllegalArgumentException.class, () -> PdfFileSource.newInstanceNoPassword(null));
    }

    @Test
    public void testDirectory() {
        File file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> PdfFileSource.newInstanceNoPassword(file));
    }

    @Test
    public void testValidFile() {
        File file = mock(File.class);
        when(file.getName()).thenReturn("name");
        when(file.isFile()).thenReturn(Boolean.TRUE);
        PdfFileSource.newInstanceNoPassword(file);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOpener() throws TaskIOException {
        PdfSourceOpener opener = mock(PdfSourceOpener.class);
        File file = mock(File.class);
        when(file.getName()).thenReturn("name");
        when(file.isFile()).thenReturn(Boolean.TRUE);
        PdfFileSource instance = PdfFileSource.newInstanceNoPassword(file);
        instance.open(opener);
        verify(opener).open(instance);
    }
    
    @Test
    public void encryptedAtRestKeepsOriginalFilename_stream() throws IOException {
        PdfStreamSource source = encryptedAtRest(
                PdfStreamSource.newInstanceNoPassword(this.getClass().getResourceAsStream("/pdf/test_file.pdf"),
                        "test_file.pdf"));
        
        assertThat(source.getSeekableSource().id(), endsWith("test_file.pdf"));
    }

    @Test
    public void encryptedAtRestKeepsOriginalFilename_file() throws IOException {
        File fileSource = new File(this.getClass().getResource("/pdf/test_file.pdf").getFile());
        PdfFileSource source = encryptedAtRest(PdfFileSource.newInstanceNoPassword(fileSource));

        assertThat(source.getSeekableSource().id(), endsWith("test_file.pdf"));
    }
}
