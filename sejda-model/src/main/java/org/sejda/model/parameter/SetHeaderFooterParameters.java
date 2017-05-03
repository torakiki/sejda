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
import java.util.*;

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
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.validation.constraint.Positive;

/**
 * Parameters configuring how to label the header/footer of a set of pages in a given pdf document.
 *
 * @author Eduard Weissmann
 *
 */
public class SetHeaderFooterParameters extends MultiplePdfSourceMultipleOutputParameters implements PageRangeSelection,
        PagesSelection {

    @NotNull
    @Valid
    private Set<PageRange> pageRanges = new HashSet<>();
    @NotNull
    private PredefinedSetOfPages predefinedSetOfPages = PredefinedSetOfPages.NONE;
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

    public PredefinedSetOfPages getPredefinedSetOfPages() {
        return predefinedSetOfPages;
    }

    public void setPredefinedSetOfPages(PredefinedSetOfPages predefinedSetOfPages) {
        this.predefinedSetOfPages = predefinedSetOfPages;
    }

    public void addPageRange(PageRange range) {
        pageRanges.add(range);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        ranges.forEach(this::addPageRange);
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    @Override
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageRanges);
    }

    public Set<PageRange> getPageRanges() {
        return pageRanges;
    }

    /**
     * @param upperLimit
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set or the natural order in
     *         case of {@link PredefinedSetOfPages}.
     * @see PagesSelection#getPages(int)
     */
    @Override
    public SortedSet<Integer> getPages(int upperLimit) {
        if (predefinedSetOfPages != PredefinedSetOfPages.NONE) {
            return predefinedSetOfPages.getPages(upperLimit);
        }
        SortedSet<Integer> retSet = new TreeSet<>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(font).append(horizontalAlign)
                .append(verticalAlign).append(fontSize).append(pageRanges).append(pattern).append(batesSequence)
                .append(pageCountStartFrom).append(color).append(fileCountStartFrom).append(addMargins).append(predefinedSetOfPages)
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
                .append(getFontSize(), parameter.getFontSize()).append(getPageRanges(), parameter.getPageRanges())
                .append(getPattern(), parameter.getPattern())
                .append(getColor(), parameter.getColor())
                .append(getFileCountStartFrom(), parameter.getFileCountStartFrom())
                .append(isAddMargins(), parameter.isAddMargins())
                .append(getPredefinedSetOfPages(), parameter.getPredefinedSetOfPages())
                .isEquals();
    }

}
