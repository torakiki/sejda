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

import java.util.List;
import java.util.regex.Pattern;

import org.sejda.model.outline.OutlineExtractPageDestinations;
import org.sejda.model.outline.OutlinePageDestinations;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * SAMBox implementation of an {@link org.sejda.model.outline.OutlineLevelsHandler}
 * 
 * @author Andrea Vacondio
 *
 */
public class SamboxOutlineLevelsHandler implements org.sejda.model.outline.OutlineLevelsHandler {

    private Pattern titleMatchingPattern = Pattern.compile(".+");
    private PDDocument document;

    public SamboxOutlineLevelsHandler(PDDocument document, String matchingTitleRegEx) {
        requireNonNull(document, "Unable to retrieve bookmarks from a null document.");
        this.document = document;
        if (isNotBlank(matchingTitleRegEx)) {
            this.titleMatchingPattern = Pattern.compile(matchingTitleRegEx);
        }
    }

    @Override
    public OutlinePageDestinations getPageDestinationsForLevel(int level) {
        OutlinePageDestinations destinations = new OutlinePageDestinations();
        OutlineUtils.getFlatOutline(document).stream().filter(i -> i.level == level).filter(i -> isNotBlank(i.title))
                .filter(i -> titleMatchingPattern.matcher(i.title).matches())
                .forEach(i -> destinations.addPage(i.page, i.title));
        return destinations;
    }

    @Override
    public OutlineExtractPageDestinations getExtractPageDestinations(int level, boolean includePageAfter) {
        OutlineExtractPageDestinations destinations = new OutlineExtractPageDestinations();

        List<OutlineItem> flatOutline = OutlineUtils.getFlatOutline(document);

        for (int i = 0; i < flatOutline.size(); i++) {
            OutlineItem item = flatOutline.get(i);
            if (item.level == level) {
                int startPage = item.page;
                String title = item.title;

                if (isNotBlank(title)) {
                    if (titleMatchingPattern.matcher(title).matches()) {
                        int endPage = document.getNumberOfPages();
                        for (int j = i + 1; j < flatOutline.size(); j++) {
                            OutlineItem after = flatOutline.get(j);
                            if (after.level <= item.level) {
                                // Looking at bookmark's xyzDestination flag is technically more accurate, but in practice outlines contain non xyzDestinations for sections that start half-page
                                // resulting in the last half page missing from the extract.

                                // Let's see. Maybe better to error on the safe side and include one extra page than have parts missing?
                                // The downside with adding one extra page is that batched payslips or any other doc that needs precise splitting will be worse
                                // with the extra page from the next item in there
                                // For now choosing the precise split and we'll see if we need to change our minds

                                // If the bookmark has a xyz destination but the output document would actually be single page, we should not include page after
                                // Eg: a payslip document where each page has a bookmark (xyz destination) that points to the page, bookmark text is employee name

                                endPage = includePageAfter ? after.page : after.page - 1;
                                // endPage = after.page;
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

}
