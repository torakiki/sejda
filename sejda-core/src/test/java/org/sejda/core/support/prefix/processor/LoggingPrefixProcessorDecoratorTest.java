/*
 * Created on 27/gen/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
