/*
 * Created on 02/gen/2011
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
package org.sejda.core.manipulation.model.pdf.label;

import java.security.InvalidParameterException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
    private PdfLabelNumberingStyle numberingStyle;
    @Min(value = 1)
    private int logicalPageNumber;
    @Min(value = 1)
    private int physicalPageNumber;

    private PdfPageLabel(String labelPrefix, PdfLabelNumberingStyle numberingStyle, int logicalPageNumber,
            int physicalPageNumber) {
        this.labelPrefix = labelPrefix;
        this.numberingStyle = numberingStyle;
        this.logicalPageNumber = logicalPageNumber;
        this.physicalPageNumber = physicalPageNumber;
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

    public int getPhysicalPageNumber() {
        return physicalPageNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(labelPrefix).append(numberingStyle).append(logicalPageNumber)
                .append(physicalPageNumber).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(labelPrefix).append(numberingStyle).append(logicalPageNumber)
                .append(physicalPageNumber).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PdfPageLabel)) {
            return false;
        }
        PdfPageLabel pageLabel = (PdfPageLabel) other;
        return new EqualsBuilder().append(labelPrefix, pageLabel.getLabelPrefix())
                .append(numberingStyle, pageLabel.getNumberingStyle())
                .append(logicalPageNumber, pageLabel.getLogicalPageNumber())
                .append(physicalPageNumber, pageLabel.getPhysicalPageNumber()).isEquals();
    }

    /**
     * Creates an empty label with the given number style for the given page number.
     * 
     * @param numberingStyle
     * @param physicalPageNumber
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input page number is not positive
     */
    public static PdfPageLabel newInstance(PdfLabelNumberingStyle numberingStyle, int physicalPageNumber) {
        return PdfPageLabel.newInstanceWithLabelAndLogicalNumber("", numberingStyle, physicalPageNumber,
                physicalPageNumber);
    }

    /**
     * Creates an empty label with the given number style for the given physical page number associated to the given logical page number.
     * 
     * @param numberingStyle
     * @param physicalPageNumber
     * @param logicalPageNumber
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input physical or logical page number is not positive
     */
    public static PdfPageLabel newInstanceWithLogicalNumber(PdfLabelNumberingStyle numberingStyle,
            int physicalPageNumber, int logicalPageNumber) {
        return PdfPageLabel.newInstanceWithLabelAndLogicalNumber("", numberingStyle, physicalPageNumber,
                logicalPageNumber);
    }

    /**
     * Creates a label with the given number style for the given physical page number.
     * 
     * @param label
     * @param numberingStyle
     * @param physicalPageNumber
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input physical or logical page number is not positive
     * @throws NullPointerException
     *             if the input label is null
     */
    public static PdfPageLabel newInstanceWithLabel(String label, PdfLabelNumberingStyle numberingStyle,
            int physicalPageNumber) {
        return PdfPageLabel.newInstanceWithLabelAndLogicalNumber(label, numberingStyle, physicalPageNumber,
                physicalPageNumber);
    }

    /**
     * Creates a label with the given number style for the given physical page number associated to the given logical page number.
     * 
     * @param label
     * @param numberingStyle
     * @param physicalPageNumber
     * @param logicalPageNumber
     * @return the newly created instance
     * @throws InvalidParameterException
     *             if the input physical or logical page number is not positive
     * @throws NullPointerException
     *             if the input label is null
     */
    public static PdfPageLabel newInstanceWithLabelAndLogicalNumber(String label,
            PdfLabelNumberingStyle numberingStyle, int physicalPageNumber, int logicalPageNumber) {
        if (physicalPageNumber < 1 || logicalPageNumber < 1) {
            throw new InvalidParameterException("Input page number must be positive.");
        }
        if (label == null) {
            throw new NullPointerException("Input label cannot be null.");
        }
        return new PdfPageLabel(label, numberingStyle, logicalPageNumber, physicalPageNumber);
    }

}
