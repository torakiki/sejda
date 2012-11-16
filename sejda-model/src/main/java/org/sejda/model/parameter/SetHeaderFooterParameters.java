/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.parameter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.headerfooter.PdfHeaderFooterLabel;
import org.sejda.model.validation.constraint.NotEmpty;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameters configuring how to label the footer of pages
 * 
 * @author Eduard Weissmann
 * 
 */
@SingleOutputAllowedExtensions
public class SetHeaderFooterParameters extends SinglePdfSourceSingleOutputParameters {

    @NotEmpty
    @Valid
    private final Map<Integer, PdfHeaderFooterLabel> labels = new HashMap<Integer, PdfHeaderFooterLabel>();
    private StandardType1Font font = StandardType1Font.HELVETICA;
    private HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
    private VerticalAlign verticalAlign = VerticalAlign.BOTTOM;
    @Min(1)
    private BigDecimal fontSize = new BigDecimal("10");

    /**
     * Apply label for all pages starting with pageNumber
     * 
     * @return previous label associated with pageNumber starting point
     */
    public PdfHeaderFooterLabel putLabel(int pageNumber, PdfHeaderFooterLabel label) {
        return this.labels.put(pageNumber, label);
    }

    /**
     * @return an unmodifiable view of the labels in this parameter.
     */
    public Map<Integer, PdfHeaderFooterLabel> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    public StandardType1Font getFont() {
        return font;
    }

    public void setFont(StandardType1Font font) {
        this.font = font;
    }

    public HorizontalAlign getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(HorizontalAlign align) {
        this.horizontalAlign = align;
    }

    public VerticalAlign getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    public BigDecimal getFontSize() {
        return fontSize;
    }

    /**
     * Set the font size in pts
     * 
     * @param fontSize
     */
    public void setFontSize(BigDecimal fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(font).append(horizontalAlign)
                .append(verticalAlign).append(fontSize).append(labels).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SetHeaderFooterParameters)) {
            return false;
        }
        SetHeaderFooterParameters parameter = (SetHeaderFooterParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(getFont(), parameter.getFont())
                .append(getHorizontalAlign(), parameter.getHorizontalAlign())
                .append(getVerticalAlign(), parameter.getVerticalAlign())
                .append(getFontSize(), parameter.getFontSize()).append(getLabels(), parameter.getLabels()).isEquals();
    }

    /**
     * @return the footer label to be applied to a pdf page number
     */
    public String formatLabelFor(int pageNumber) {
        int labelDefStartPage = getLabelDefinitionStartPageFor(pageNumber);
        if (labelDefStartPage <= 0) {
            return null;
        }

        int offset = pageNumber - labelDefStartPage;
        PdfHeaderFooterLabel label = getLabels().get(labelDefStartPage);
        return label.formatFor(offset);
    }

    /**
     * Find a page number x, for starting with which, the user defined a label that should be applied also to input pageNumber Eg: user defines label1 for pages starting at 10 and
     * label2 for pages starting with 100. key page for 12 would be 1, key page for 101 would be 100, key page for 9 would be 0
     */
    private int getLabelDefinitionStartPageFor(int pageNumber) {
        if (pageNumber <= 0) {
            return pageNumber;
        }

        if (labels.containsKey(pageNumber)) {
            return pageNumber;
        }
        return getLabelDefinitionStartPageFor(findHighestStartPageLowerThan(pageNumber - 1));
    }

    private int findHighestStartPageLowerThan(int page) {
        int prevStartPage = 0;
        for (int startPage : new TreeSet<Integer>(labels.keySet())) {
            if (startPage > page) {
                return prevStartPage;
            }

            prevStartPage = startPage;
        }

        return prevStartPage;
    }

}
