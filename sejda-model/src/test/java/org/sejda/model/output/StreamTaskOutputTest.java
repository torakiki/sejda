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
package org.sejda.model.output;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.OutputStream;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class StreamTaskOutputTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullStream() {
        new StreamTaskOutput(null);
    }

    @Test
    public void testValidStream() {
        OutputStream stream = mock(OutputStream.class);
        StreamTaskOutput instance = new StreamTaskOutput(stream);
        assertNotNull(instance);
    }

    @Test
    public void testEquals() {
        OutputStream stream = mock(OutputStream.class);
        OutputStream diffStream = mock(OutputStream.class);
        StreamTaskOutput eq1 = new StreamTaskOutput(stream);
        StreamTaskOutput eq2 = new StreamTaskOutput(stream);
        StreamTaskOutput eq3 = new StreamTaskOutput(stream);
        StreamTaskOutput diff = new StreamTaskOutput(diffStream);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
