/*
 * Created on 01/lug/2010
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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * Process the input prefix replacing all the [BOOKMARK_NAME] occurrences with the input bookmark name if any.
 * 
 * @author Andrea Vacondio
 * 
 */
class BookmarkPrefixProcessor implements PrefixProcessor {

    private static final String BOOKMARK_NAME_REPLACE_REGX = "\\[BOOKMARK_NAME\\]";
    private static final String INVALID_WIN_FILENAME_CHARS_REGEXP = "[\\\\/:*?\\\"<>|]";

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        if (request != null && StringUtils.isNotBlank(request.getBookmark())) {
            String bookmarkName = request.getBookmark().replaceAll(INVALID_WIN_FILENAME_CHARS_REGEXP, "");
            if (StringUtils.isNotBlank(bookmarkName)) {
                return retVal.replaceAll(BOOKMARK_NAME_REPLACE_REGX, bookmarkName);
            }
        }
        return retVal;
    }

}
