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
package org.sejda.core.support.perfix.processor;



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
public enum PrefixType {

    BASENAME(false, "\\[BASENAME\\]", BasenamePrefixProcessor.class),
    CURRENTPAGE(true, "\\[CURRENTPAGE(#*)\\]", CurrentPagePrefixProcessor.class),
    FILENUMBER(true, "\\[FILENUMBER(#*)(\\d*)\\]", FileNumberPrefixProcessor.class),
    TIMESTAMP(true, "\\[TIMESTAMP\\]", TimestampPrefixProcessor.class),
    BOOKMARK(true, "\\[BOOKMARK_NAME\\]", BookmarkPrefixProcessor.class);

    private boolean ensureUniqueNames;
    private String matchingRegexp;
    private Class<? extends PrefixProcessor> processor;

    private PrefixType(boolean ensureUniqueNames, String matchingRegexp, Class<? extends PrefixProcessor> processor) {
        this.ensureUniqueNames = ensureUniqueNames;
        this.matchingRegexp = matchingRegexp;
        this.processor = processor;
    }

    /**
     * @return true if this prefix type ensures a unique names generation
     */
    public boolean isEnsureUniqueNames() {
        return ensureUniqueNames;
    }

    /**
     * @return the regexp to find in a prefix string.
     */
    public String getMatchingRegexp() {
        return matchingRegexp;
    }

    /**
     * @return the processor for this prefix type
     */
    public Class<? extends PrefixProcessor> getProcessor() {
        return processor;
    }

}
