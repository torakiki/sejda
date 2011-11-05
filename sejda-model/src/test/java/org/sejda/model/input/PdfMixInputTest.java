/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.input.PdfMixInput.PdfMixInputProcessStatus;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfMixInputTest {

    private PdfSource<?> source;

    @Before
    public void setUp() {
        source = mock(PdfSource.class);
    }

    @Test
    public void testStatus() {
        PdfMixInput victim = new PdfMixInput(source);
        PdfMixInputProcessStatus status = victim.newProcessingStatus(10);
        assertTrue(status.hasNextPage());
        assertEquals(1, status.nextPage());
    }

    @Test
    public void testStatusReverse() {
        PdfMixInput victim = new PdfMixInput(source, true, 1);
        PdfMixInputProcessStatus status = victim.newProcessingStatus(10);
        assertTrue(status.hasNextPage());
        assertEquals(10, status.nextPage());
    }

    @Test
    public void testStatusNoNextPage() {
        PdfMixInput victim = new PdfMixInput(source);
        PdfMixInputProcessStatus status = victim.newProcessingStatus(2);
        assertTrue(status.hasNextPage());
        assertEquals(1, status.nextPage());
        assertTrue(status.hasNextPage());
        assertEquals(2, status.nextPage());
        assertFalse(status.hasNextPage());
    }
}
