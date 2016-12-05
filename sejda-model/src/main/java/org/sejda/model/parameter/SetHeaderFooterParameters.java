/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
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
package org.sejda.model.parameter;

import java.awt.Color;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.numbering.BatesSequence;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.validation.constraint.Positive;

/**
 * Parameters configuring how to label the header/footer of a set of pages in a given pdf document.
 *
 * @author Eduard Weissmann
 *
 */
public class SetHeaderFooterParameters extends MultiplePdfSourceMultipleOutputParameters {

    @NotNull
    @Valid
    private PageRange pageRange = new PageRange(1);
    private StandardType1Font font = StandardType1Font.HELVETICA;
    private HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
    private VerticalAlign verticalAlign = VerticalAlign.BOTTOM;
    @Positive
    private double fontSize = 10d;
    @NotNull
    private String pattern;
    private Integer pageCountStartFrom;
    private BatesSequence batesSequence;
    @NotNull
    private Color color = Color.black;
    private int fileCountStartFrom = 1;
    private boolean addMargins = false;

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

    public double getFontSize() {
        return fontSize;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer getPageCountStartFrom() {
        return pageCountStartFrom;
    }

    public void setPageCountStartFrom(int pageCountStartFrom) {
        this.pageCountStartFrom = pageCountStartFrom;
    }

    public BatesSequence getBatesSequence() {
        return batesSequence;
    }

    public void setBatesSequence(BatesSequence batesSequence) {
        this.batesSequence = batesSequence;
    }

    /**
     * Set the font size in pts
     *
     * @param fontSize
     */
    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getFileCountStartFrom() {
        return fileCountStartFrom;
    }

    public void setFileCountStartFrom(Integer fileCountStartFrom) {
        this.fileCountStartFrom = fileCountStartFrom;
    }

    public boolean isAddMargins() {
        return addMargins;
    }

    public void setAddMargins(boolean addMargins) {
        this.addMargins = addMargins;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(font).append(horizontalAlign)
                .append(verticalAlign).append(fontSize).append(pageRange).append(pattern).append(batesSequence)
                .append(pageCountStartFrom).append(color).append(fileCountStartFrom).append(addMargins)
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
                .append(getBatesSequence(), parameter.getBatesSequence())
                .append(getPageCountStartFrom(), parameter.getPageCountStartFrom())
                .append(getFontSize(), parameter.getFontSize()).append(getPageRange(), parameter.getPageRange())
                .append(getPattern(), parameter.getPattern())
                .append(getColor(), parameter.getColor())
                .append(getFileCountStartFrom(), parameter.getFileCountStartFrom())
                .append(isAddMargins(), parameter.isAddMargins())
                .isEquals();
    }

}
