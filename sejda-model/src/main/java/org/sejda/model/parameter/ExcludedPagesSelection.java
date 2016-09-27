/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.Collection;
import java.util.Set;

import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.pdf.page.PageRange;

public interface ExcludedPagesSelection {

    Set<PageRange> getExcludedPagesSelection();

    default void addExcludedPage(Integer page) {
        addExcludedPageRange(new PageRange(page, page));
    }

    default void addExcludedPageRange(PageRange range) {
        getExcludedPagesSelection().add(range);
    }

    default void addAllExcludedPageRanges(Collection<PageRange> ranges) {
        getExcludedPagesSelection().addAll(ranges);
    }

    /**
     * @param upperLimit
     *            the number of pages of the document (upper limit).
     * @return the set of excluded pages.
     */
    default Set<Integer> getExcludedPages(int upperLimit) {
        Set<Integer> pages = new NullSafeSet<>();
        for (PageRange range : getExcludedPagesSelection()) {
            pages.addAll(range.getPages(upperLimit));
        }
        return pages;
    }

}
