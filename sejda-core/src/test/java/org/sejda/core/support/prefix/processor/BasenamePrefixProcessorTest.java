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

import org.junit.jupiter.api.Test;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

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
        var prefix = "prefix_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest());
        victim.accept(context);
        assertEquals(prefix, context.currentPrefix());
    }

    @Test
    public void testComplexProcess() {
        var prefix = "prefix_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().originalName("name"));
        victim.accept(context);
        assertEquals("prefix_name", context.currentPrefix());
    }

    @Test
    public void testComplexProcessStripExtension() {
        var prefix = "prefix_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().originalName("name.pdf"));
        victim.accept(context);
        assertEquals("prefix_name", context.currentPrefix());
    }

    @Test
    public void testComplexProcessStripExtensionOneCharName() {
        var prefix = "prefix_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().originalName("x.pdf"));
        victim.accept(context);
        assertEquals("prefix_x", context.currentPrefix());
    }

    @Test
    public void testUnescapedRegexInBasename() {
        var prefix = "[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().originalName("x$5.pdf"));
        victim.accept(context);
        assertEquals("x$5", context.currentPrefix());
    }

    @Override
    PrefixProcessor getProcessor() {
        return victim;
    }
}
