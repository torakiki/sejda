/*
 * Created on 03/ago/2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import java.util.Set;

import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.page.PagesSelection;

/**
 * Skeletal implementation for a split by page parameter class.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractSplitByPageParameters extends SinglePdfSourceMultipleOutputParameters implements
        PagesSelection {

    /**
     * @param upperLimit
     *            upper limit for the pages set.
     * @return the set of pages to split at. All pages are greater then 0 and lesser then upperLimit.
     */
    public abstract Set<Integer> getPages(int upperLimit);

}
