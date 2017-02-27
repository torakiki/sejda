/*
 * Created on 06 dic 2016
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
package org.sejda.model.parameter;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.validation.constraint.NotEmpty;

import javax.validation.Valid;

/**
 * Parameter class to extract text by pages performing OCR
 * 
 * @author Andrea Vacondio
 *
 */
public class OcrTextByPagesParameters extends SinglePdfSourceMultipleOutputParameters
        implements PageRangeSelection, PagesSelection {

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();
    @NotEmpty
    private String textEncoding = "UTF-8";

    public final Set<Locale> languages = new HashSet<>();

    /**
     * Adds a language o the list of possible languages of the text found in the document. This can help the OCR engine to return a more accurate result.
     *
     * @param language
     */
    public void addLanguage(Locale language) {
        this.languages.add(language);
    }

    /**
     * @return Languages that can be fed to the OCR engine to return a more accurate result
     */
    public Set<Locale> getLanguages() {
        return languages;
    }

    public String getTextEncoding() {
        return textEncoding;
    }

    public void setTextEncoding(String textEncoding) {
        this.textEncoding = textEncoding;
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    @Override
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    public void addPageRange(PageRange range) {
        pageSelection.add(range);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        pageSelection.addAll(ranges);
    }

    /**
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set.
     * @see PagesSelection#getPages(int)
     */
    @Override
    public Set<Integer> getPages(int totalNumberOfPage) {
        if (pageSelection.isEmpty()) {
            return new PageRange(1).getPages(totalNumberOfPage);
        }
        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(totalNumberOfPage));
        }
        return retSet;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(pageSelection).append(textEncoding)
                .append(languages).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OcrTextByPagesParameters)) {
            return false;
        }
        OcrTextByPagesParameters parameter = (OcrTextByPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(pageSelection, parameter.pageSelection)
                .append(textEncoding, parameter.getTextEncoding())
                .append(languages, parameter.getLanguages())
                .isEquals();
    }
}
