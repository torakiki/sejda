/*
 * Created on 23/gen/2011
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
package org.sejda.impl.itext.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.core.manipulation.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.core.manipulation.model.pdf.label.PdfPageLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfPageLabels;

/**
 * Utility class to deal with page labels.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PageLabelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PageLabelUtils.class);

    private static final Map<PdfLabelNumberingStyle, Integer> PAGE_NUMBERS_STYLES;
    static {
        Map<PdfLabelNumberingStyle, Integer> pageNumberStyles = new HashMap<PdfLabelNumberingStyle, Integer>();
        pageNumberStyles.put(PdfLabelNumberingStyle.ARABIC, PdfPageLabels.DECIMAL_ARABIC_NUMERALS);
        pageNumberStyles.put(PdfLabelNumberingStyle.EMPTY, PdfPageLabels.EMPTY);
        pageNumberStyles.put(PdfLabelNumberingStyle.LOWERCASE_LETTERS, PdfPageLabels.LOWERCASE_LETTERS);
        pageNumberStyles.put(PdfLabelNumberingStyle.LOWERCASE_ROMANS, PdfPageLabels.LOWERCASE_ROMAN_NUMERALS);
        pageNumberStyles.put(PdfLabelNumberingStyle.UPPERCASE_LETTERS, PdfPageLabels.UPPERCASE_LETTERS);
        pageNumberStyles.put(PdfLabelNumberingStyle.UPPERCASE_ROMANS, PdfPageLabels.UPPERCASE_ROMAN_NUMERALS);
        PAGE_NUMBERS_STYLES = Collections.unmodifiableMap(pageNumberStyles);
    }

    private PageLabelUtils() {
        // utility
    }

    /**
     * Maps a map of {@link PdfPageLabel} to a {@link PdfPageLabels} instance that can be used as input for the PdfCopy.
     * 
     * @param labels
     * @param totalPages
     * @return the resulting {@link PdfPageLabels}
     */
    public static PdfPageLabels getLabels(Map<Integer, PdfPageLabel> labels, int totalPages) {
        PdfPageLabels retVal = new PdfPageLabels();
        for (Entry<Integer, PdfPageLabel> entry : labels.entrySet()) {
            PdfPageLabel label;
            if (entry.getKey() <= totalPages) {
                label = entry.getValue();
                retVal.addPageLabel(entry.getKey(), PAGE_NUMBERS_STYLES.get(label.getNumberingStyle()),
                        label.getLabelPrefix(), label.getLogicalPageNumber());
            } else {
                LOG.warn("Page number out of rage, {} will be ignored.", entry.getValue());
            }
        }
        return retVal;
    }
}
