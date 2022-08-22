/*
 * Created on 23/gen/2011
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
package org.sejda.model.parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter class for the set pages label manipulation. The manipulation will apply a label to physical pages until a new label for a physical page number is found.
 * <p>
 * Ex. if the parameters contain two labels, "label1" for physical page 1 and "label2" for physical page 5, the resulting document will have pages 1 to 4 where "label1" is applied
 * and pages 5 till the end of the document where "label2" is applied.
 * 
 * @author Andrea Vacondio
 * 
 */
@SingleOutputAllowedExtensions
public class SetPagesLabelParameters extends SinglePdfSourceSingleOutputParameters {

    @NotEmpty
    @Valid
    private final Map<Integer, PdfPageLabel> labels = new HashMap<Integer, PdfPageLabel>();

    /**
     * Associates the given label to the given page number. If a label was already associated to the given page, it is replaced with the new one.
     * 
     * @param page
     *            the one based page number
     * @param label
     * @return the previously associated label or null.
     */
    public PdfPageLabel putLabel(Integer page, PdfPageLabel label) {
        return labels.put(page, label);
    }

    /**
     * @return an unmodifiable view of the labels in this parameter.
     */
    public Map<Integer, PdfPageLabel> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(labels).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SetPagesLabelParameters)) {
            return false;
        }
        SetPagesLabelParameters parameter = (SetPagesLabelParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(getLabels(), parameter.getLabels())
                .isEquals();
    }

}
