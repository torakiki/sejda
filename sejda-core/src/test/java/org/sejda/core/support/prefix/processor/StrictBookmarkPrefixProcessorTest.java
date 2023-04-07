/*
 * Created on 07/ott/2011
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

import org.junit.jupiter.api.Test;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * @author Andrea Vacondio
 */
public class StrictBookmarkPrefixProcessorTest extends BasePrefixProcessorTest {
    private StrictBookmarkPrefixProcessor victim = new StrictBookmarkPrefixProcessor();

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void nullBookmarks() {
        var prefix = "prefix_[BOOKMARK_NAME_STRICT]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest());
        victim.accept(context);
        assertEquals(prefix, context.currentPrefix());
    }

    @Test
    public void testComplexProcess() {
        var prefix = "prefix_[BOOKMARK_NAME_STRICT]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book name here"));
        victim.accept(context);
        assertEquals("prefix_book name here_[BASENAME]", context.currentPrefix());
    }

    @Test
    public void testComplexProcessInvalidChars() {
        var prefix = "prefix_[BOOKMARK_NAME_STRICT]_[BASENAME]";
        var context = new PrefixTransformationContext(prefix, nameRequest().bookmark("book<>?$ç°"));
        victim.accept(context);
        assertEquals("prefix_book_[BASENAME]", context.currentPrefix());
    }
}
