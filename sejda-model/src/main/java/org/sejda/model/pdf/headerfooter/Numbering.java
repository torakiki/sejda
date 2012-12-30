/*
 * Created on 29/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
    @Min(value = 1)
    private int logicalPageNumber; // start numbering from

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
