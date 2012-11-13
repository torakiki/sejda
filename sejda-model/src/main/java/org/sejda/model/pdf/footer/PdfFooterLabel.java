/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.pdf.footer;

import java.security.InvalidParameterException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

/**
 * Labeling information for the footer of the page
 * 
 * @author Eduard Weissmann
 */
public final class PdfFooterLabel {
    @NotNull
    private String labelPrefix;
    @NotNull
    private FooterNumberingStyle numberingStyle;
    @Min(value = 1)
    private int logicalPageNumber; // start numbering from

    private PdfFooterLabel(String labelPrefix, FooterNumberingStyle numberingStyle, int logicalPageNumber) {
        this.labelPrefix = StringUtils.defaultString(labelPrefix, "");
        if (numberingStyle == null) {
            throw new InvalidParameterException("Input numbering style cannot be null.");
        }
        this.numberingStyle = numberingStyle;
        this.logicalPageNumber = logicalPageNumber;
    }

    public String getLabelPrefix() {
        return labelPrefix;
    }

    public FooterNumberingStyle getNumberingStyle() {
        return numberingStyle;
    }

    public int getLogicalPageNumber() {
        return logicalPageNumber;
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder().append(this.labelPrefix)
                .append(this.numberingStyle).append(this.logicalPageNumber).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PdfFooterLabel other = (PdfFooterLabel) obj;
        return new org.apache.commons.lang3.builder.EqualsBuilder().append(this.labelPrefix, other.labelPrefix)
                .append(this.numberingStyle, other.numberingStyle)
                .append(this.logicalPageNumber, other.logicalPageNumber).isEquals();
    }

    public String formatFor(int offset) {
        return String.format("%s%s", labelPrefix, numberingStyle.toStyledString(logicalPageNumber + offset)).trim();
    }

    public static PdfFooterLabel newInstanceNoLabelPrefix(FooterNumberingStyle numberingStyle, int logicalPageNumber) {
        return new PdfFooterLabel(null, numberingStyle, logicalPageNumber);
    }

    public static PdfFooterLabel newInstanceTextOnly(String labelPrefix) {
        return new PdfFooterLabel(labelPrefix, FooterNumberingStyle.EMPTY, 1);
    }

    public static PdfFooterLabel newInstanceWithLabelPrefixAndNumbering(String labelPrefix,
            FooterNumberingStyle numberingStyle, int logicalPageNumber) {
        return new PdfFooterLabel(labelPrefix, numberingStyle, logicalPageNumber);
    }
}
