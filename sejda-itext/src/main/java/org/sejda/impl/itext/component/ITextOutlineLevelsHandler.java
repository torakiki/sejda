/*
 * Created on 07/ago/2011
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

    public int getMaxOutlineDepth() {
        return getMaxBookmarkLevel(bookmarks, 0);
    }

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
