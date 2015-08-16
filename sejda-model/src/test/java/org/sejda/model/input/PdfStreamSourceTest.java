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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.model.exception.TaskIOException;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfStreamSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullStream() {
        PdfStreamSource.newInstanceWithPassword(null, "fdsfs", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        InputStream stream = mock(InputStream.class);
        PdfStreamSource.newInstanceWithPassword(stream, null, null);
    }

    @Test
    public void testValidStream() {
        InputStream stream = mock(InputStream.class);
        PdfStreamSource instance = PdfStreamSource.newInstanceWithPassword(stream, "dsadsada", "dsdasdsa");
        assertNotNull(instance);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOpener() throws TaskIOException {
        PdfSourceOpener opener = mock(PdfSourceOpener.class);
        InputStream stream = mock(InputStream.class);
        PdfStreamSource instance = PdfStreamSource.newInstanceWithPassword(stream, "dsadsada", "dsdasdsa");
        instance.open(opener);
        verify(opener).open(instance);
    }
}
