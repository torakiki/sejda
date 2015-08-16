/*
 * Created on 09/giu/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
public final class ITextOutlineUtils {

    public static final String GOTO_VALUE = "GoTo";
    public static final String ACTION_KEY = "Action";
    public static final String PAGE_KEY = "Page";
    public static final String KIDS_KEY = "Kids";
    public static final String TITLE_KEY = "Title";
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
        return bookmark != null && GOTO_VALUE.equals(bookmark.get(ACTION_KEY));
    }
}
