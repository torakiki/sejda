/*
 * Created on 09/giu/2013
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
package org.sejda.impl.itext.component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class providing outline handling helper methods
 * 
 * @author Andrea Vacondio
 * 
 */
final class ITextOutlineUtils {

    static final String GOTO_KEY = "GoTo";
    static final String ACTION_KEY = "Action";
    static final String PAGE_KEY = "Page";
    static final String KIDS_KEY = "Kids";
    static final String TITLE_KEY = "Title";
    private static final Pattern PAGE_NUMBER_MATCHING_PATTERN = Pattern.compile("(\\d+)(.*)");

    private ITextOutlineUtils() {
        // utility
    }

    static int getMaxBookmarkLevel(List<Map<String, Object>> bookmarks, int parentLevel) {
        int maxLevel = parentLevel;
        if (bookmarks != null) {
            for (Map<String, Object> bookmark : bookmarks) {
                if (isGoToAction(bookmark)) {
                    @SuppressWarnings("unchecked")
                    int maxBookmarkBranchLevel = getMaxBookmarkLevel(
                            (List<Map<String, Object>>) bookmark.get(KIDS_KEY), parentLevel + 1);
                    if (maxBookmarkBranchLevel > maxLevel) {
                        maxLevel = maxBookmarkBranchLevel;
                    }
                }
            }
        }
        return maxLevel;
    }

    static String nullSafeGetTitle(Map<String, Object> bookmark) {
        if (bookmark != null) {
            return ObjectUtils.toString(bookmark.get(TITLE_KEY));
        }
        return StringUtils.EMPTY;
    }

    static int getPageNumber(Map<String, Object> bookmark) {
        Object page = bookmark.get(PAGE_KEY);
        if (page == null) {
            return -1;
        }
        Matcher matcher = PAGE_NUMBER_MATCHING_PATTERN.matcher(page.toString());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    static boolean isGoToAction(Map<String, Object> bookmark) {
        return bookmark != null && GOTO_KEY.equals(bookmark.get(ACTION_KEY));
    }
}
