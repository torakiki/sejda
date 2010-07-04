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

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.perfix.NameGenerationRequest.nameRequest;

import org.junit.Test;
import org.sejda.core.support.perfix.processor.BookmarkPrefixProcessor;
import org.sejda.core.support.perfix.processor.PrefixProcessor;

/**
 * Test unit for {@link BookmarkPrefixProcessor}
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

}
