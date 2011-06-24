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
package org.sejda.core.manipulation.model.input;

import java.io.InputStream;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

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
}
