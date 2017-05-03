/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public abstract class BasePrefixProcessorTest {

    /**
     * Test that the process method returns the input prefix in case of empty request and a simple prefix.
     */
    @Test
    public void testEmptyRequestSimplePrefix() {
        assertEquals("prefix", getProcessor().process("prefix", nameRequest()));
    }

    @Test
    public void nullRequest() {
        assertEquals("prefix_", getProcessor().process("prefix_", null));
    }

    abstract PrefixProcessor getProcessor();
}
