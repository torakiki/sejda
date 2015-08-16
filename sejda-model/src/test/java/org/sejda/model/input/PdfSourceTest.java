/*
 * Created on 30/ott/2011
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
package org.sejda.model.input;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfSourceTest {

    private File file;

    @Before
    public void setUp() {
        file = mock(File.class);
        when(file.getName()).thenReturn("name");
        when(file.isFile()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void getPasswordBytes() {
        PdfFileSource victim = PdfFileSource.newInstanceWithPassword(file, "pdf");
        assertNotNull(victim.getPasswordBytes());
        victim = PdfFileSource.newInstanceWithPassword(file, "");
        assertNull(victim.getPasswordBytes());
    }
}
