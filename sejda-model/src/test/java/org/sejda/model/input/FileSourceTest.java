/*
 * Created on 28 feb 2016
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
package org.sejda.model.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class FileSourceTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNullFile() {
        FileSource.newInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectory() {
        File file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.FALSE);
        FileSource.newInstance(file);
    }

    @Test
    public void testValidFile() {
        File file = mock(File.class);
        when(file.getName()).thenReturn("Chuck");
        when(file.isFile()).thenReturn(Boolean.TRUE);
        FileSource instance = FileSource.newInstance(file);
        assertNotNull(instance);
        assertEquals("Chuck", instance.getName());
        assertEquals(file, instance.getSource());
    }
}
