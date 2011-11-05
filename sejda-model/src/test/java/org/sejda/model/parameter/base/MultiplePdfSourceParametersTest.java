/*
 * Created on 17/set/2010
 *
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
package org.sejda.model.parameter.base;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.TaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class MultiplePdfSourceParametersTest {

    private MultiplePdfSourceParameters victim;

    @Before
    public void setUp() {
        victim = new MultiplePdfSourceParameters() {

            @Override
            public TaskOutput getOutput() {
                return null;
            }

            @Override
            public void setOutput(TaskOutput output) {
                // nothing
            }

        };
    }

    @Test
    public void testAdd() {

        PdfSource<?> source = mock(PdfSource.class);
        victim.addSource(source);
        assertEquals(1, victim.getSourceList().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableList() {
        PdfSource<?> source = mock(PdfSource.class);
        victim.addSource(source);
        victim.getSourceList().clear();
    }
}
