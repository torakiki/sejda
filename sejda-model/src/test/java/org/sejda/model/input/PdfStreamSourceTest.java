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

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfStreamSourceTest {

    @Test
    public void testNullStream() {
        assertThrows(IllegalArgumentException.class,
                () -> PdfStreamSource.newInstanceWithPassword(null, "fdsfs", null));
    }

    @Test
    public void testNullName() {
        var stream = mock(InputStream.class);
        assertThrows(IllegalArgumentException.class, () -> PdfStreamSource.newInstanceWithPassword(stream, null, null));
    }

    @Test
    public void testValidStream() {
        var stream = mock(InputStream.class);
        var instance = PdfStreamSource.newInstanceWithPassword(stream, "dsadsada", "dsdasdsa");
        assertNotNull(instance);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOpener() throws TaskIOException {
        var opener = mock(PdfSourceOpener.class);
        var stream = mock(InputStream.class);
        PdfStreamSource instance = PdfStreamSource.newInstanceWithPassword(stream, "dsadsada", "dsdasdsa");
        instance.open(opener);
        verify(opener).open(instance);
    }
}
