/*
 * Created on 09/ago/2011
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
package org.sejda.model.split;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.sejda.model.exception.TaskExecutionException;

/**
 * Strategy that holds the page numbers where the split process has to split.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitPages implements NextOutputStrategy {

    private Set<Integer> closingPages = new HashSet<Integer>();
    private Set<Integer> openingPages = new HashSet<Integer>();

    public SplitPages(Collection<Integer> pages) {
        this(pages.toArray(new Integer[pages.size()]));

    }

    public SplitPages(Integer... pages) {
        openingPages.add(1);
        for (Integer page : pages) {
            add(page);
        }
    }

    void add(Integer page) {
        closingPages.add(page);
        openingPages.add(page + 1);
    }

    @Override
    public void ensureIsValid() throws TaskExecutionException {
        if (closingPages.isEmpty()) {
            throw new TaskExecutionException("Unable to split, no page number given.");
        }
    }

    /**
     * @param page
     * @return true if the given page is an opening page (a page where the split process should start a new document).
     */
    @Override
    public boolean isOpening(Integer page) {
        return openingPages.contains(page);
    }

    /**
     * @param page
     * @return true if the given page is an closing page (a page where the split process should close the document).
     */
    @Override
    public boolean isClosing(Integer page) {
        return closingPages.contains(page);
    }
}
