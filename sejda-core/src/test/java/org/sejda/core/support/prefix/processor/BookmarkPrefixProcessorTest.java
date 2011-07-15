/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;

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
