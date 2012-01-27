/*
 * Created on 27/gen/2012
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
package org.sejda.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Closeable;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class ComponentsUtilityTest {

    @Test
    public void testNullSafeClose() throws IOException {
        ComponentsUtility.nullSafeClose(null);
        Closeable mock = mock(Closeable.class);
        ComponentsUtility.nullSafeClose(mock);
        verify(mock).close();
    }
}
