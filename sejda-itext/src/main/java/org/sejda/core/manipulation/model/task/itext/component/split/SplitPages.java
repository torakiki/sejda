/*
 * Created on 02/jul/2011
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
package org.sejda.core.manipulation.model.task.itext.component.split;

import java.util.HashSet;
import java.util.Set;

import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.task.itext.component.split.AbstractPdfSplitter.NextOutputStrategy;

/**
 * Strategy that holds the page numbers where the split process has to split.
 * 
 * @author Andrea Vacondio
 * 
 */
class SplitPages implements NextOutputStrategy {

    private Set<Integer> closingPages = new HashSet<Integer>();
    private Set<Integer> openingPages = new HashSet<Integer>();

    SplitPages(Integer totalPages) {
        if (totalPages < 1) {
            throw new IllegalArgumentException(String.format("Total number of pages %d must be positive", totalPages));
        }
        closingPages.add(totalPages);
        openingPages.add(1);
    }

    /**
     * Adds a page to split at.
     * 
     * @param page
     */
    void add(Integer page) {
        closingPages.add(page);
        openingPages.add(page + 1);
    }

    public void ensureIsValid() throws TaskExecutionException {
        if (closingPages.size() <= 1) {
            throw new TaskExecutionException("Unable to split, no page number given.");
        }
    }

    /**
     * @param page
     * @return true if the given page is an opening page (a page where the split process should start a new document).
     */
    public boolean isOpening(Integer page) {
        return openingPages.contains(page);
    }

    /**
     * @param page
     * @return true if the given page is an closing page (a page where the split process should close the document).
     */
    public boolean isClosing(Integer page) {
        return closingPages.contains(page);
    }
}
