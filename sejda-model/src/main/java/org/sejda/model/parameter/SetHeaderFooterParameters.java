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

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.headerfooter.Numbering;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.validation.constraint.HasHeaderFooterDefinition;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameters configuring how to label the header/footer of a set of pages in a given pdf document.
 * 
 * @author Eduard Weissmann
 * 
 */
@SingleOutputAllowedExtensions
@HasHeaderFooterDefinition
public class SetHeaderFooterParameters extends SinglePdfSourceSingleOutputParameters {

    @NotNull
    @Valid
    private PageRange pageRange;
    private StandardType1Font font = StandardType1Font.HELVETICA;
    private HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
    private VerticalAlign verticalAlign = VerticalAlign.BOTTOM;
    @Min(1)
    private BigDecimal fontSize = BigDecimal.TEN;
    @NotNull
    @Valid
    private Numbering numbering = Numbering.NULL;
    private String labelPrefix;

    public PageRange getPageRange() {
        return pageRange;
    }

    /**
     * Set the page range where the header/footer will be applied
     * 
     * @param pageRange
     */
    public void setPageRange(PageRange pageRange) {
        this.pageRange = pageRange;
    }

    /**
     * 
     * @param pageNumber
     * @return the styled label for the given page number
     */
    public String styledLabelFor(int pageNumber) {
        return String.format("%s%s", defaultString(labelPrefix, ""), numbering.styledLabelFor(pageNumber)).trim();
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

    public Numbering getNumbering() {
        return numbering;
    }

    public void setNumbering(Numbering numbering) {
        if (numbering == null) {
            this.numbering = Numbering.NULL;
        } else {
            this.numbering = numbering;
        }
    }

    public String getLabelPrefix() {
        return labelPrefix;
    }

    public void setLabelPrefix(String labelPrefix) {
        this.labelPrefix = labelPrefix;
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
                .append(verticalAlign).append(fontSize).append(pageRange).append(numbering).append(labelPrefix)
                .toHashCode();
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
                .append(getFontSize(), parameter.getFontSize()).append(getPageRange(), parameter.getPageRange())
                .append(numbering, parameter.getNumbering()).append(labelPrefix, parameter.getLabelPrefix()).isEquals();
    }

}
