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
 * Test unit for {@link BookmarkPrefixProcessor}
 * 
 * @author Andrea Vacondio
 * 
 */
public class BookmarkPrefixProcessorTest extends BasePrefixProcessorTest {

    private BookmarkPrefixProcessor victim = new BookmarkPrefixProcessor();

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void nullBookmarks() {
        String prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        assertEquals(prefix, victim.process(prefix, nameRequest()));
    }

    @Test
    public void testComplexProcess() {
        String prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        String bookmark = "book";
        String expected = "prefix_book_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().bookmark(bookmark)));
    }

    @Test
    public void testComplexProcessInvalidChars() {
        String prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        String bookmark = "book<>?";
        String expected = "prefix_book_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().bookmark(bookmark)));
    }

    @Test
    public void testUnescapedRegexGroup() {
        String prefix = "[BOOKMARK_NAME]";
        String bookmark = "book$5";
        String expected = "book$5";
        assertEquals(expected, victim.process(prefix, nameRequest().bookmark(bookmark)));
    }

}
