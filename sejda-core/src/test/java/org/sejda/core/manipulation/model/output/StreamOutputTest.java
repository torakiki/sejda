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
package org.sejda.core.manipulation.model.output;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.OutputStream;

import org.junit.Test;
import org.sejda.core.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class StreamOutputTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullStream() {
        StreamOutput.newInstance(null);
    }

    @Test
    public void testValidStream() {
        OutputStream stream = mock(OutputStream.class);
        StreamOutput instance = StreamOutput.newInstance(stream);
        assertNotNull(instance);
    }

    @Test
    public void testEquals() {
        OutputStream stream = mock(OutputStream.class);
        OutputStream diffStream = mock(OutputStream.class);
        StreamOutput eq1 = StreamOutput.newInstance(stream);
        StreamOutput eq2 = StreamOutput.newInstance(stream);
        StreamOutput eq3 = StreamOutput.newInstance(stream);
        StreamOutput diff = StreamOutput.newInstance(diffStream);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
