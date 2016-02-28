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

import java.io.InputStream;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class StreamSourceTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNullStream() {
        StreamSource.newInstance(null, "fdsfs");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        InputStream stream = mock(InputStream.class);
        StreamSource.newInstance(stream, null);
    }

    @Test
    public void testValidStream() {
        InputStream stream = mock(InputStream.class);
        StreamSource instance = StreamSource.newInstance(stream, "Chuck");
        assertNotNull(instance);
        assertEquals("Chuck", instance.getName());
        assertEquals(stream, instance.getSource());
    }
}
