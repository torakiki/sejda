/*
 * Created on 01/lug/2010
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

/**
 * Process the input prefix replacing all the [BOOKMARK_NAME] occurrences with the input bookmark name if any. A small set of possibly invalid characters is trimmed from the the
 * bookmark value.
 * 
 * @author Andrea Vacondio
 * 
 */
class BookmarkPrefixProcessor extends BaseBookmarkPrefixProcessor {

    private static final String BOOKMARK_NAME_REPLACE_REGX = "\\[BOOKMARK_NAME\\]";
    private static final String INVALID_WIN_FILENAME_CHARS_REGEXP = "[\\\\/:*?\\\"<>|]";

    public BookmarkPrefixProcessor() {
        super(BOOKMARK_NAME_REPLACE_REGX, INVALID_WIN_FILENAME_CHARS_REGEXP);
    }
}
