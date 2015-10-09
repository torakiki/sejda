/* 
 * This file is part of the Sejda source code
 * Created on 09/mar/2015
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sejda.impl.sambox.component.OutlineUtils.getMaxOutlineLevel;
import static org.sejda.impl.sambox.component.OutlineUtils.toPageDestination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sejda.model.outline.OutlinePageDestinations;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

/**
 * SAMBox implementation of an {@link org.sejda.model.outline.OutlineLevelsHandler}
 * 
 * @author Andrea Vacondio
 *
 */
public class SamboxOutlineLevelsHandler implements org.sejda.model.outline.OutlineLevelsHandler {

    private Pattern titleMatchingPattern = Pattern.compile(".+");
    private PDDocument document;
    private PDPageTree pages;

    public SamboxOutlineLevelsHandler(PDDocument document, String matchingTitleRegEx) {
        requireNonNull(document, "Unable to retrieve bookmarks from a null document.");
        this.document = document;
        this.pages = document.getPages();
        if (isNotBlank(matchingTitleRegEx)) {
            this.titleMatchingPattern = Pattern.compile(matchingTitleRegEx);
        }
    }

    @Override
    public int getMaxOutlineDepth() {
        return getMaxOutlineLevel(document);
    }

    @Override
    public OutlinePageDestinations getPageDestinationsForLevel(int level) {
        OutlinePageDestinations destinations = new OutlinePageDestinations();
        addPageIfOutlineLevel(document.getDocumentCatalog().getDocumentOutline(), 1, destinations, level);
        return destinations;
    }

    private void addPageIfOutlineLevel(PDOutlineNode outline, int currentLevel, OutlinePageDestinations destinations,
            int levelToAdd) {
        if (outline != null) {
            for (PDOutlineItem current : outline.children())
                if (currentLevel <= levelToAdd) {
                    toPageDestination(current, document.getDocumentCatalog()).ifPresent(d -> {
                        if (isLevelToBeAdded(currentLevel, levelToAdd)) {
                            addPageIfValid(destinations, d, current.getTitle());
                        } else {
                            addPageIfOutlineLevel(current, currentLevel + 1, destinations, levelToAdd);
                        }
                    });
                }
        }
    }

    private boolean isLevelToBeAdded(int currentLevel, int levelToAdd) {
        return currentLevel == levelToAdd;
    }

    private void addPageIfValid(OutlinePageDestinations destinations, PDPageDestination destination, String title) {
        if (isNotBlank(title)) {
            Matcher matcher = titleMatchingPattern.matcher(title);
            if (matcher.matches()) {
                destinations.addPage(pages.indexOf(destination.getPage()) + 1, title);
            }
        }
    }
}
