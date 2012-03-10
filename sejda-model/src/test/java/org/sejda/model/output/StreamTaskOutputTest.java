/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
