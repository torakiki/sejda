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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
}
