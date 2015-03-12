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
package org.sejda.model.outline;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holder for a collection of document outline page destinations. Keeps information about the association between the destinations and the outline title associated with that.
 * 
 * @author Andrea Vacondio
 */
public class OutlinePageDestinations {

    private Map<Integer, String> destinations = new HashMap<Integer, String>();

    /**
     * Adds the given page destination with the given title.
     * 
     * @param page
     * @param title
     * @throws IllegalArgumentException
     *             if the page number is null or negative.
     */
    public void addPage(Integer page, String title) {
        if (page == null) {
            throw new IllegalArgumentException("Unable to add a null page to the destinations.");
        }
        destinations.put(page, title);
    }

    /**
     * @return an unmodifiable view of the set of pages.
     */
    public Set<Integer> getPages() {
        return Collections.unmodifiableSet(destinations.keySet());
    }

    /**
     * @param page
     * @return the title corresponding to the input page.
     */
    public String getTitle(Integer page) {
        return destinations.get(page);
    }
}
