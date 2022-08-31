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

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andrea Vacondio
 */
public class PdfURLSourceTest {

    @Test
    public void testNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> PdfURLSource.newInstanceWithPassword(null, "fdsfs", null));
    }

    @Test
    public void testNullName() throws MalformedURLException {
        var url = new URL("https://www.sejda.org");
        assertThrows(IllegalArgumentException.class, () -> PdfURLSource.newInstanceWithPassword(url, null, null));
    }

    @Test
    public void testValidStream() throws MalformedURLException {
        var url = new URL("https://www.sejda.org");
        PdfURLSource instance = PdfURLSource.newInstanceWithPassword(url, "dsadsada", "dsdasdsa");
        assertNotNull(instance);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOpener() throws MalformedURLException, TaskIOException {
        var opener = mock(PdfSourceOpener.class);
        var url = new URL("https://www.sejda.org");
        PdfURLSource instance = PdfURLSource.newInstanceNoPassword(url, "dsadsada");
        instance.open(opener);
        verify(opener).open(instance);
    }
}
