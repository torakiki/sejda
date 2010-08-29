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
