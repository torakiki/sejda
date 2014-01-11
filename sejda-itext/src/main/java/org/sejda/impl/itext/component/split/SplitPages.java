/*
 * Created on 09/ago/2011
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
package org.sejda.impl.itext.component.split;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.sejda.impl.itext.component.split.AbstractPdfSplitter.NextOutputStrategy;
import org.sejda.model.exception.TaskExecutionException;

/**
 * Strategy that holds the page numbers where the split process has to split.
 * 
 * @author Andrea Vacondio
 * 
 */
class SplitPages implements NextOutputStrategy {

    private Set<Integer> closingPages = new HashSet<Integer>();
    private Set<Integer> openingPages = new HashSet<Integer>();

    SplitPages(Collection<Integer> pages) {
        openingPages.add(1);
        for (Integer page : pages) {
            add(page);
        }
    }

    private void add(Integer page) {
        closingPages.add(page - 1);
        openingPages.add(page);
    }

    public void ensureIsValid() throws TaskExecutionException {
        if (closingPages.isEmpty()) {
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
