/*
 * Created on 02/gen/2011
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
package org.sejda.model.pdf.label;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.security.InvalidParameterException;

import static org.sejda.commons.util.RequireUtils.requireArg;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * Model for a page label. <br>
 * Pdf reference 1.7, Chap. 8.3.1
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfPageLabel {

    @NotNull
    private String labelPrefix;
    @NotNull
    private PdfLabelNumberingStyle numberingStyle;
    @Min(value = 1)
    private int logicalPageNumber;

    private PdfPageLabel(String labelPrefix, PdfLabelNumberingStyle numberingStyle, int logicalPageNumber) {
        this.labelPrefix = labelPrefix;
        this.numberingStyle = numberingStyle;
        this.logicalPageNumber = logicalPageNumber;
    }

    public String getLabelPrefix() {
        return labelPrefix;
    }

    public PdfLabelNumberingStyle getNumberingStyle() {
        return numberingStyle;
    }

    public int getLogicalPageNumber() {
        return logicalPageNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(labelPrefix).append(numberingStyle)
                .append("logicalPageNumber", logicalPageNumber).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(labelPrefix).append(numberingStyle).append(logicalPageNumber).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PdfPageLabel pageLabel)) {
            return false;
        }
        return new EqualsBuilder().append(labelPrefix, pageLabel.getLabelPrefix())
                .append(numberingStyle, pageLabel.getNumberingStyle())
                .append(logicalPageNumber, pageLabel.getLogicalPageNumber()).isEquals();
    }

    /**
     * Creates an empty label with the given style for the given logical page number.
     * 
     * @param numberingStyle
     * @param logicalPageNumber
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input logical page number is not positive. if the input numbering style is null.
     */
    public static PdfPageLabel newInstanceWithoutLabel(PdfLabelNumberingStyle numberingStyle, int logicalPageNumber) {
        return PdfPageLabel.newInstanceWithLabel("", numberingStyle, logicalPageNumber);
    }

    /**
     * Creates a label with given label and given style for the given logical page number.
     *
     * @param label
     * @param numberingStyle
     * @param logicalPageNumber
     * @return the newly created instance
     * @throws IllegalArgumentException if the input logical page number is not positive. if the input label or numbering style are null.
     */
    public static PdfPageLabel newInstanceWithLabel(String label, PdfLabelNumberingStyle numberingStyle,
            int logicalPageNumber) {
        requireArg(logicalPageNumber > 0, "Input page number must be positive");
        requireNotNullArg(label, "Input label cannot be null");
        requireNotNullArg(numberingStyle, "Input numbering style cannot be null");
        return new PdfPageLabel(label, numberingStyle, logicalPageNumber);
    }

}
