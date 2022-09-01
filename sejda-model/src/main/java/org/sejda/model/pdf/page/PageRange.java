/*
 * Created on 29/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.pdf.page;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.validation.constraint.EndGreaterThenOrEqualToStart;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Model for range of pages.
 * 
 * @author Andrea Vacondio
 * 
 */
@EndGreaterThenOrEqualToStart
public class PageRange implements PagesSelection {

    private static final int UNBOUNDED_END = Integer.MAX_VALUE;

    @Min(1)
    private int start;
    @Min(1)
    private int end;

    PageRange() {
        // default constructor for persistence
    }

    /**
     * Creates a page range that goes from start to end (comprehended).
     * 
     * @param start
     * @param end
     */
    public PageRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Creates a page range that starts at the given page and ends when the document ends.
     * 
     * @param start
     */
    public PageRange(int start) {
        this.start = start;
        this.end = UNBOUNDED_END;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    /**
     * @return true of this page range has a start but not an end.
     */
    public boolean isUnbounded() {
        return this.end == UNBOUNDED_END;
    }

    /**
     * @param range
     * @return <code>true</code> if the input range intersect this {@link PageRange} instance.
     */
    public boolean intersects(PageRange range) {
        return (range.getStart() >= start && range.getStart() <= end)
                || (range.getEnd() >= start && range.getEnd() <= end);
    }

    /**
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages ordered using their natural ordering.
     * @see PagesSelection#getPages(int)
     */
    @Override
    public SortedSet<Integer> getPages(int totalNumberOfPage) {
        SortedSet<Integer> retSet = new TreeSet<>();
        for (int i = start; i <= totalNumberOfPage && i <= end; i++) {
            retSet.add(i);
        }
        return retSet;
    }

    public boolean contains(int page) {
        return start <= page && end >= page;
    }

    @Override
    public String toString() {
        if (isUnbounded()) {
            return String.format("%s-", start);
        }

        if (start == end) {
            return String.format("%s", start);
        }

        return String.format("%s-%s", start, end);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(start).append(end).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PageRange range)) {
            return false;
        }
        return new EqualsBuilder().append(start, range.getStart()).append(end, range.getEnd()).isEquals();
    }

    public static PageRange one(int page) {
        return new PageRange(page, page);
    }

}
