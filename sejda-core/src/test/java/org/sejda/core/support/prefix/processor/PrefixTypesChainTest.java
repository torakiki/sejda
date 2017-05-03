/*
 * Created on 03 mag 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class PrefixTypesChainTest {
    @Test
    public void current() {
        String prefix = "prefix_[CURRENTPAGE]";
        assertEquals("prefix_5.pdf",
                new PrefixTypesChain(prefix).process(prefix, nameRequest().originalName("name").page(5)));
    }

    @Test
    public void currentAndBasename() {
        String prefix = "prefix_[CURRENTPAGE]_[BASENAME]";
        assertEquals("prefix_5_name.pdf",
                new PrefixTypesChain(prefix).process(prefix, nameRequest().originalName("name").page(5)));
    }

    @Test
    public void noComplexPrefixYesPage() {
        String prefix = "prefix_";
        assertEquals("5_prefix_name.pdf",
                new PrefixTypesChain(prefix).process(prefix, nameRequest().originalName("name").page(5)));
    }

    @Test
    public void noComplexPrefixNoPage() {
        String prefix = "prefix_";
        assertEquals("prefix_name.pdf",
                new PrefixTypesChain(prefix).process(prefix, nameRequest().originalName("name")));
    }

    @Test
    public void basenameYesPage() {
        String prefix = "prefix_[BASENAME]";
        // no uniqueness, page is prepended
        assertEquals("5_prefix_name.pdf",
                new PrefixTypesChain(prefix).process(prefix, nameRequest().originalName("name").page(5)));
    }

    @Test
    public void basenameNoPage() {
        String prefix = "prefix_[BASENAME]";
        assertEquals("prefix_name.pdf",
                new PrefixTypesChain(prefix).process(prefix, nameRequest().originalName("name")));
    }

    @Test
    public void filenumberAndExtension() {
        String prefix = "prefix_[FILENUMBER]_[BASENAME]";
        assertEquals("prefix_3_name.txt", new PrefixTypesChain(prefix).process(prefix,
                nameRequest("txt").originalName("name.pdf").fileNumber(3)));
    }
}
