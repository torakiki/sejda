/*
 * Created on 07/ago/2011
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
package org.sejda.impl.itext.component;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sejda.impl.itext.component.ITextOutlineUtils.KIDS_KEY;
import static org.sejda.impl.itext.component.ITextOutlineUtils.getMaxBookmarkLevel;
import static org.sejda.impl.itext.component.ITextOutlineUtils.getPageNumber;
import static org.sejda.impl.itext.component.ITextOutlineUtils.isGoToAction;
import static org.sejda.impl.itext.component.ITextOutlineUtils.nullSafeGetTitle;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sejda.model.outline.OutlineLevelsHandler;
import org.sejda.model.outline.OutlinePageDestinations;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * iText implementation of an {@link OutlineLevelsHandler}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineLevelsHandler implements OutlineLevelsHandler {

    private Pattern titleMatchingPattern = Pattern.compile(".+");
    private List<Map<String, Object>> bookmarks;

    @SuppressWarnings({ "cast", "unchecked" })
    public ITextOutlineLevelsHandler(PdfReader reader, String matchingTitleRegEx) {
        reader.consolidateNamedDestinations();
        this.bookmarks = (List<Map<String, Object>>) SimpleBookmark.getBookmark(reader);
        if (isNotBlank(matchingTitleRegEx)) {
            titleMatchingPattern = Pattern.compile(matchingTitleRegEx);
        }
    }

    @Override
    public int getMaxOutlineDepth() {
        return getMaxBookmarkLevel(bookmarks, 0);
    }

    @Override
    public OutlinePageDestinations getPageDestinationsForLevel(int goToActionLevel) {
        OutlinePageDestinations destinations = new OutlinePageDestinations();
        addPageIfBookmarkLevel(bookmarks, 1, destinations, goToActionLevel);
        return destinations;
    }

    @SuppressWarnings("unchecked")
    private void addPageIfBookmarkLevel(List<Map<String, Object>> bookmarks, int currentLevel,
            OutlinePageDestinations destinations, int levelToAdd) {
        if (bookmarks != null) {
            for (Map<String, Object> bookmark : bookmarks) {
                if (currentLevel <= levelToAdd && isGoToAction(bookmark)) {
                    if (isLevelToBeAdded(currentLevel, levelToAdd)) {
                        addPageIfValid(destinations, bookmark);
                    } else {
                        addPageIfBookmarkLevel((List<Map<String, Object>>) bookmark.get(KIDS_KEY), currentLevel + 1,
                                destinations, levelToAdd);
                    }
                }
            }
        }
    }

    private boolean isLevelToBeAdded(int currentLevel, int levelToAdd) {
        return currentLevel == levelToAdd;
    }

    private void addPageIfValid(OutlinePageDestinations destinations, Map<String, Object> bookmark) {
        int page = getPageNumber(bookmark);
        String title = nullSafeGetTitle(bookmark);
        Matcher matcher = titleMatchingPattern.matcher(title);
        if (page != -1 && matcher.matches()) {
            destinations.addPage(page, title);
        }
    }

}
