/*
 * Created on 29/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.pdf.headerfooter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * immutable model for a numbering that is defined by a style and the starting page number.
 * 
 * @author Andrea Vacondio
 * 
 */
public class Numbering {

    public static final Numbering NULL = new Numbering(NumberingStyle.EMPTY, 1);

    @NotNull
    private NumberingStyle numberingStyle;

    // start numbering from
    @Min(value = 1)
    private int logicalPageNumber;

    public Numbering(NumberingStyle numberingStyle, int logicalPageNumber) {
        if (numberingStyle == null) {
            throw new IllegalArgumentException("Input numbering style cannot be null.");
        }
        this.numberingStyle = numberingStyle;
        this.logicalPageNumber = logicalPageNumber;
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder().append(this.numberingStyle)
                .append(this.logicalPageNumber).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Numbering other = (Numbering) obj;
        return new EqualsBuilder().append(this.numberingStyle, other.numberingStyle)
                .append(this.logicalPageNumber, other.logicalPageNumber).isEquals();
    }

    public NumberingStyle getNumberingStyle() {
        return numberingStyle;
    }

    public int getLogicalPageNumber() {
        return logicalPageNumber;
    }

    /**
     * 
     * @param pageNumber
     * @return the styled label for the given page number
     */
    public String styledLabelFor(int pageNumber) {
        return numberingStyle.toStyledString(pageNumber).trim();
    }
}
