/*
 * Created on 07/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

/**
 * Process the input prefix replacing all the [BOOKMARK_NAME_STRICT] occurrences with the input bookmark name if any. All the character that are not a letter or a number or '_' are
 * trimmed from the the bookmark value.
 * 
 * @author Andrea Vacondio
 * 
 */
class StrictBookmarkPrefixProcessor extends BaseBookmarkPrefixProcessor {

    private static final String BOOKMARK_NAME_REPLACE_REGX = "\\[BOOKMARK_NAME_STRICT\\]";
    private static final String INVALID_WIN_FILENAME_CHARS_REGEXP = "(?i)[^A-Z0-9_]";

    public StrictBookmarkPrefixProcessor() {
        super(BOOKMARK_NAME_REPLACE_REGX, INVALID_WIN_FILENAME_CHARS_REGEXP);
    }
}
