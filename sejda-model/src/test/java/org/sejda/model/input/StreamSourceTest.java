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

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 *
 */
public class StreamSourceTest {
    @Test
    public void testNullStream() {
        assertThrows(IllegalArgumentException.class, () -> StreamSource.newInstance(null, "fdsfs"));
    }

    @Test
    public void testNullName() {
        var stream = mock(InputStream.class);
        assertThrows(IllegalArgumentException.class, () -> StreamSource.newInstance(stream, null));
    }

    @Test
    public void testValidStream() {
        var stream = mock(InputStream.class);
        var instance = StreamSource.newInstance(stream, "Chuck");
        assertNotNull(instance);
        assertEquals("Chuck", instance.getName());
        assertEquals(stream, instance.getSource());
    }
}
