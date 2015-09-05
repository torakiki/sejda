/*
 * Created on 10/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.outline.OutlinePageDestinations;

/**
 * Strategy to define opening and closing page numbers from an {@link OutlinePageDestinations}. The logic behind is that if a destination points to page x and the user splits at
 * the level, he probably wants to split at page x-1 (i.e. the end of the preceding chapter or paragraph), resulting in a pdf document per chapter (or paragraph...).
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageDestinationsSplitPages implements NextOutputStrategy {

    private SplitPages delegate = new SplitPages();

    public PageDestinationsSplitPages(OutlinePageDestinations destinations) {
        for (Integer page : destinations.getPages()) {
            delegate.add(page - 1);
        }
    }

    @Override
    public void ensureIsValid() throws TaskExecutionException {
        delegate.ensureIsValid();
    }

    @Override
    public boolean isOpening(Integer page) {
        return delegate.isOpening(page);
    }

    @Override
    public boolean isClosing(Integer page) {
        return delegate.isClosing(page);
    }

}
