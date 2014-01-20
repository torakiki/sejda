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
    BOOKMARK_STRICT(true, "\\[BOOKMARK_NAME_STRICT\\]", new StrictBookmarkPrefixProcessor());

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
