/*
 * Created on 18 dic 2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.sambox.pdmodel.common.PDPageLabelRange;
import org.sejda.sambox.pdmodel.common.PDPageLabels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to deal with page labels.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PageLabelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PageLabelUtils.class);

    private static final Map<PdfLabelNumberingStyle, String> PAGE_NUMBERS_STYLES;

    static {
        Map<PdfLabelNumberingStyle, String> pageNumberStyles = new HashMap<>();
        pageNumberStyles.put(PdfLabelNumberingStyle.ARABIC, PDPageLabelRange.STYLE_DECIMAL);
        pageNumberStyles.put(PdfLabelNumberingStyle.LOWERCASE_LETTERS, PDPageLabelRange.STYLE_LETTERS_LOWER);
        pageNumberStyles.put(PdfLabelNumberingStyle.LOWERCASE_ROMANS, PDPageLabelRange.STYLE_ROMAN_LOWER);
        pageNumberStyles.put(PdfLabelNumberingStyle.UPPERCASE_LETTERS, PDPageLabelRange.STYLE_LETTERS_UPPER);
        pageNumberStyles.put(PdfLabelNumberingStyle.UPPERCASE_ROMANS, PDPageLabelRange.STYLE_ROMAN_UPPER);
        PAGE_NUMBERS_STYLES = Collections.unmodifiableMap(pageNumberStyles);
    }

    private PageLabelUtils() {
        // utility
    }

    /**
     * Maps a map of {@link PdfPageLabel} to a {@link PDPageLabels} instance.
     * 
     * @param labels
     *            one based page numbers
     * @param totalPages
     * @return the resulting {@link PDPageLabels}
     */
    public static PDPageLabels getLabels(Map<Integer, PdfPageLabel> labels, int totalPages) {
        PDPageLabels retLabels = new PDPageLabels();
        for (Entry<Integer, PdfPageLabel> entry : labels.entrySet()) {
            int page = entry.getKey();
            if (page > 0 && page <= totalPages) {
                PdfPageLabel label = entry.getValue();
                PDPageLabelRange range = new PDPageLabelRange();
                range.setStyle(PAGE_NUMBERS_STYLES.get(label.getNumberingStyle()));
                range.setStart(label.getLogicalPageNumber());
                range.setPrefix(label.getLabelPrefix());
                retLabels.setLabelItem(page - 1, range);
            } else {
                LOG.warn("Page number {} out of rage, {} will be ignored.", page, entry.getValue());
            }
        }
        return retLabels;
    }

    // TODO: understand how logicalPage should be affected
    public static PDPageLabels removePages(PDPageLabels pageLabels, List<Integer> pagesToRemove, int totalPages) {
        Map<Integer, PDPageLabelRange> labels = new TreeMap<>(pageLabels.getLabels());

        List<Integer> pagesToRemoveSortedLastFirst = new ArrayList<>(pagesToRemove);
        pagesToRemoveSortedLastFirst.sort(Collections.reverseOrder());

        // go backwards from last to first
        // why? otherwise pagesToRemove would need to be shifted -1 after each page removal
        for(int pageToRemove : pagesToRemoveSortedLastFirst) {
            // make a copy to avoid ConcurrentModificationException
            Map<Integer, PDPageLabelRange> updatedLabels = new TreeMap<>();
            // pagesToRemove are 1-based, indices 0-based
            int pageIndex = pageToRemove - 1;

            for(int key : labels.keySet()) {
                if(key <= pageIndex) {
                    // just copy over as is
                    updatedLabels.put(key, labels.get(key));
                } else if(key > pageIndex) {
                    // shift index - 1
                    int prevKey = key - 1;
                    if (prevKey >= 0) {
                        updatedLabels.put(prevKey, labels.get(key));
                    }
                }
            }

            // overwrite
            labels = updatedLabels;
        }

        // calculate the new page total
        int newTotalPages = totalPages - pagesToRemove.size();

        PDPageLabels result = new PDPageLabels();
        for(int index: labels.keySet()) {
            // exclude the last label range, if index is larger that new total page num
            if(index < newTotalPages) {
                result.setLabelItem(index, labels.get(index));
            }
        }
        return result;
    }
}
