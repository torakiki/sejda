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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sejda.model.outline.OutlineExtractPageDestinations;
import org.sejda.model.outline.OutlinePageDestinations;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of an {@link org.sejda.model.outline.OutlineLevelsHandler}
 * 
 * @author Andrea Vacondio
 *
 */
public class SamboxOutlineLevelsHandler implements org.sejda.model.outline.OutlineLevelsHandler {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SamboxOutlineLevelsHandler.class);

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

    @Override
    public OutlineExtractPageDestinations getExtractPageDestinations(int level) {
        OutlineExtractPageDestinations destinations = new OutlineExtractPageDestinations();

        List<OutlineItem> flatOutline = getFlatOutline();

        for(int i = 0; i < flatOutline.size(); i++) {
            OutlineItem item = flatOutline.get(i);
            if(item.level == level) {
                int startPage = item.page;
                String title = item.title;

                if (isNotBlank(title)) {
                    if (titleMatchingPattern.matcher(title).matches()) {
                        int endPage = document.getNumberOfPages();
                        for(int j = i + 1; j < flatOutline.size(); j++) {
                            OutlineItem after = flatOutline.get(j);
                            if(after.level <= item.level) {
                                // This is technically more accurate, but in practice outlines contain non xyzDestinations for sections that start half-page
                                // resulting in the last half page missing from the extract. better to error on the safe side and include one extra page than have parts missing
                                //endPage = after.xyzDestination ? after.page : after.page - 1;
                                endPage = after.page;
                                break;
                            }
                        }
                        destinations.add(startPage, title, endPage);
                    }
                }
            }
        }

        return destinations;
    }

    private List<OutlineItem> getFlatOutline() {
        PDOutlineNode outline = document.getDocumentCatalog().getDocumentOutline();

        if(outline == null) return new ArrayList<>();

        List<OutlineItem> result = recurseFlatOutline(outline.children(), 1);
        Collections.sort(result);

        return result;
    }

    private List<OutlineItem> recurseFlatOutline(Iterable<PDOutlineItem> items, int level) {
        List<OutlineItem> result = new ArrayList<>();
        for(PDOutlineItem item: items) {
            toPageDestination(item, document.getDocumentCatalog()).ifPresent(d -> {
                PDPage page = d.getPage();
                int pageNumber;
                if(page != null){
                    pageNumber = pages.indexOf(page) + 1 /* 0-based index */;
                } else {
                    pageNumber = d.getPageNumber();
                }
                boolean specificLocation = d instanceof PDPageXYZDestination;
                result.add(new OutlineItem(item.getTitle(), pageNumber, level, specificLocation));

            });
            result.addAll(recurseFlatOutline(item.children(), level + 1));
        }

        return result;
    }
}
