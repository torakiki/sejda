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

import java.util.regex.Pattern;

/**
 * Enum for the types of prefix. It contains information about the prefix type like:
 * <ul>
 * <li>if it ensure unique output names</li>
 * <li>the regexp to match to find this prefix type</li>
 * <li>the processor</li>
 * </ul>
 * 
 * @author Andrea Vacondio
 * @see PrefixProcessor
 * 
 */
enum PrefixType {

    BASENAME(false, "\\[BASENAME\\]", new BasenamePrefixProcessor()),
    CURRENTPAGE(true, "\\[CURRENTPAGE(#*)(-?[0-9]*)\\]", new CurrentPagePrefixProcessor()),
    FILENUMBER(true, "\\[FILENUMBER(#*)(-?[0-9]*)\\]", new FileNumberPrefixProcessor()),
    TIMESTAMP(true, "\\[TIMESTAMP\\]", new TimestampPrefixProcessor()),
    BOOKMARK(true, "\\[BOOKMARK_NAME\\]", new BookmarkPrefixProcessor()),
    BOOKMARK_STRICT(true, "\\[BOOKMARK_NAME_STRICT\\]", new StrictBookmarkPrefixProcessor()),
    TEXT(true, "\\[TEXT\\]", new TextPrefixProcessor());

    private boolean ensureUniqueNames;
    private Pattern pattern;
    private PrefixProcessor processor;

    private PrefixType(boolean ensureUniqueNames, String matchingRegexp, PrefixProcessor processor) {
        this.ensureUniqueNames = ensureUniqueNames;
        this.pattern = Pattern.compile(matchingRegexp);
        this.processor = processor;
    }

    /**
     * @return true if this prefix type ensures a unique names generation
     */
    public boolean isEnsureUniqueNames() {
        return ensureUniqueNames;
    }

    public boolean isFoundIn(String toBeSearched) {
        return pattern.matcher(toBeSearched).find();
    }

    /**
     * @return the processor for this prefix type
     */
    public PrefixProcessor getProcessor() {
        return processor;
    }

}
