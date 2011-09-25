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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.sejda.core.exception.TaskIOException;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfURLSourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullUrl() {
        PdfURLSource.newInstanceWithPassword(null, "fdsfs", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() throws MalformedURLException {
        URL url = new URL("http://www.sejda.org");
        PdfURLSource.newInstanceWithPassword(url, null, null);
    }

    @Test
    public void testValidStream() throws MalformedURLException {
        URL url = new URL("http://www.sejda.org");
        PdfURLSource instance = PdfURLSource.newInstanceWithPassword(url, "dsadsada", "dsdasdsa");
        assertNotNull(instance);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOpener() throws MalformedURLException, TaskIOException {
        PdfSourceOpener opener = mock(PdfSourceOpener.class);
        URL url = new URL("http://www.sejda.org");
        PdfURLSource instance = PdfURLSource.newInstanceNoPassword(url, "dsadsada");
        instance.open(opener);
        verify(opener).open(instance);
    }
}
