/*
 * Created on 26/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import java.util.Set;

/**
 * Specify a set of selected pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface PagesSelection {

    /**
     * 
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. No constraint is imposed on the predictability of the iteration order but implementors may explicitly state if grant predictable iteration
     *         order.
     */
    Set<Integer> getPages(int totalNumberOfPage);
}
