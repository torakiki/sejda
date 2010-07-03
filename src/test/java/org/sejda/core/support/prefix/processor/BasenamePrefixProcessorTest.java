/*
 * Created on 03/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.support.prefix.processor;

import static junit.framework.Assert.assertEquals;
import static org.sejda.core.support.perfix.NameGenerationRequest.nameRequest;

import org.junit.Test;
import org.sejda.core.support.perfix.processor.BasenamePrefixProcessor;
import org.sejda.core.support.perfix.processor.PrefixProcessor;

/**
 * Test case for BasenamePrefixProcessor
 * @author Andrea Vacondio
 *
 */
public class BasenamePrefixProcessorTest extends BasePrefixProcessorTest{

    private BasenamePrefixProcessor victim = new BasenamePrefixProcessor();
    
    @Test
    public void testComplexProcess(){
        String prefix = "prefix_[BASENAME]";
        String originalName = "name";
        String expected = "prefix_name";
        assertEquals(expected, victim.process(prefix, nameRequest().originalName(originalName)));
    }

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }
}
