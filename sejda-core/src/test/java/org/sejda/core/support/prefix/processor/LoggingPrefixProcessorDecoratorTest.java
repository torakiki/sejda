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
package org.sejda.core.support.prefix.processor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * @author Andrea Vacondio
 * 
 */
public class LoggingPrefixProcessorDecoratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalConstructor() {
        new LoggingPrefixProcessorDecorator(null);
    }

    @Test
    public void testProcess() {
        PrefixProcessor decorated = mock(PrefixProcessor.class);
        LoggingPrefixProcessorDecorator victim = new LoggingPrefixProcessorDecorator(decorated);
        NameGenerationRequest request = NameGenerationRequest.nameRequest();
        victim.process("", request);
        verify(decorated).process("", request);
    }
}
