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
 * @author Andrea Vacondio
 */
public class CurrentPagePrefixProcessorTest extends BasePrefixProcessorTest {

    private CurrentPagePrefixProcessor victim = new CurrentPagePrefixProcessor();
    private final int page = 5;

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void nullPage() {
        var prefix = "prefix_[CURRENTPAGE]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest());
        victim.accept(context);
        assertEquals(prefix, context.currentPrefix());
    }

    @Test
    public void testComplexProcess() {
        var prefix = "prefix_[CURRENTPAGE]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_5_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testComplexProcessStartingPage() {
        var prefix = "prefix_[CURRENTPAGE12]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_17_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testComplexProcessWithPatterStartingPage() {
        var prefix = "prefix_[CURRENTPAGE###10]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_015_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testComplexProcessWithPatter() {
        var prefix = "prefix_[CURRENTPAGE###]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_005_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testComplexProcessDouble() {
        var prefix = "prefix_[CURRENTPAGE]_[CURRENTPAGE]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_5_5", context.currentPrefix());
    }

    @Test
    public void testComplexProcessDoubleSinglePattern() {
        var prefix = "prefix_[CURRENTPAGE###]_[CURRENTPAGE]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_005_5", context.currentPrefix());
    }

    @Test
    public void testComplexProcessDoubleSinglePatternStartingPage() {
        var prefix = "prefix_[CURRENTPAGE###23]_[CURRENTPAGE32]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_028_37", context.currentPrefix());
    }

    @Test
    public void testComplexProcessDoubleSinglePatternNegativeStartingPage() {
        var prefix = "prefix_[CURRENTPAGE###-23]_[CURRENTPAGE-5]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_-018_0", context.currentPrefix());
    }

    @Test
    public void testComplexProcessDoubleDoublePattern() {
        var prefix = "prefix_[CURRENTPAGE###]_[CURRENTPAGE##]";
        var context = new PrefixTransformationContext(prefix, nameRequest().page(page));
        victim.accept(context);
        assertEquals("prefix_005_05", context.currentPrefix());
    }
}
