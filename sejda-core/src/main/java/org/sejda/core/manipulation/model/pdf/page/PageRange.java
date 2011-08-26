/*
 * Created on 29/mag/2010
 *
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
package org.sejda.core.manipulation.model.pdf.page;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Min;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.validation.constraint.EndGreaterThenOrEqualToStart;

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

    /**
     * Creates a page range that goes from start to end.
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
        return ((range.getStart() >= start && range.getStart() <= end) || (range.getEnd() >= start && range.getEnd() <= end));
    }

    public Set<Integer> getPages(int totalNumberOfPage) {
        Set<Integer> retSet = new HashSet<Integer>();
        for (int i = start; (i <= totalNumberOfPage && i <= end); i++) {
            retSet.add(i);
        }
        return retSet;
    }

    @Override
    public String toString() {
        if (isUnbounded()) {
            return String.format("%s-", start);
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
        if (!(other instanceof PageRange)) {
            return false;
        }
        PageRange range = (PageRange) other;
        return new EqualsBuilder().append(start, range.getStart()).append(end, range.getEnd()).isEquals();
    }

}
