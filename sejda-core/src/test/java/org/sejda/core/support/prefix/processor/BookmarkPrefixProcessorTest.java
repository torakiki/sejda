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

    @Test
    public void testNewlineInBookmark() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book\nmark"));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testCarriageReturnInBookmark() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book\rmark"));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testCrLfInBookmark() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book\r\nmark"));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testTabInBookmark() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book\tmark"));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testNulCharInBookmark() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book\u0000mark"));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testOtherControlCharsInBookmark() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        // vertical tab, form feed, escape, DEL
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("bo\u000Bok\u000Cma\u001Br\u007Fk"));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testPathSeparators() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book/mark\\name"));
        victim.accept(context);
        assertEquals("prefix_bookmarkname_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testLeadingTrailingWhitespace() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("  bookmark  "));
        victim.accept(context);
        assertEquals("prefix_bookmark_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testUnicodeBookmarkPreserved() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("bókmärk 日本語"));
        victim.accept(context);
        assertEquals("prefix_bókmärk 日本語_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testBackslashReplacementLiteral() {
        var prefix = "[BOOKMARK_NAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book\\$1mark"));
        victim.accept(context);
        assertEquals("book$1mark", context.currentPrefix());
    }

    @Test
    public void testFrenchAccentedCharsPreserved() {
        var prefix = "prefix_[BOOKMARK_NAME]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix,
                nameRequest().bookmark("Résumé de l'été à Noël, château être français où déjà cœur naïve garçon"));
        victim.accept(context);
        assertEquals("prefix_Résumé de l'été à Noël, château être français où déjà cœur naïve garçon_[BASENAME]",
                context.currentPrefix());
    }
}
