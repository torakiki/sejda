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
 * Test unit for {@link BookmarkPrefixProcessor}
 *
 * @author Andrea Vacondio
 */
public class BookmarkPrefixProcessorTest extends BasePrefixProcessorTest {

    private BookmarkPrefixProcessor victim = new BookmarkPrefixProcessor();

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void nullBookmarks() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest());
        victim.accept(context);
        assertEquals(prefix, context.currentPrefix());
    }

    @Test
    public void testComplexProcess() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book"));
        victim.accept(context);
        assertEquals("prefix_book_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testComplexProcessInvalidChars() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book<>?"));
        victim.accept(context);
        assertEquals("prefix_book_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testUnescapedRegexGroup() {
        var prefix = "[BOOKMARK_NAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book$5"));
        victim.accept(context);
        assertEquals("book$5", context.currentPrefix());
    }

}
