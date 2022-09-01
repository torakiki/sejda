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

    private Map<Integer, String> destinations = new HashMap<>();

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
