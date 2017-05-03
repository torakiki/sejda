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
 * Test case for BasenamePrefixProcessor
 * 
 * @author Andrea Vacondio
 * 
 */
public class BasenamePrefixProcessorTest extends BasePrefixProcessorTest {

    private BasenamePrefixProcessor victim = new BasenamePrefixProcessor();

    @Test
    public void nullName() {
        String prefix = "prefix_[BASENAME]";
        assertEquals(prefix, victim.process(prefix, nameRequest()));
    }

    @Test
    public void testComplexProcess() {
        String prefix = "prefix_[BASENAME]";
        String originalName = "name";
        String expected = "prefix_name";
        assertEquals(expected, victim.process(prefix, nameRequest().originalName(originalName)));
    }

    @Test
    public void testComplexProcessStripExtension() {
        String prefix = "prefix_[BASENAME]";
        String originalName = "name.pdf";
        String expected = "prefix_name";
        assertEquals(expected, victim.process(prefix, nameRequest().originalName(originalName)));
    }

    @Test
    public void testComplexProcessStripExtensionOneCharName() {
        String prefix = "prefix_[BASENAME]";
        String originalName = "x.pdf";
        String expected = "prefix_x";
        assertEquals(expected, victim.process(prefix, nameRequest().originalName(originalName)));
    }

    @Test
    public void testUnescapedRegexInBasename() {
        String prefix = "[BASENAME]";
        String originalName = "x$5.pdf";
        String expected = "x$5";
        assertEquals(expected, victim.process(prefix, nameRequest().originalName(originalName)));
    }

    @Override
    PrefixProcessor getProcessor() {
        return victim;
    }
}
