/*
 * Created on 17/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.validation.constraint.NotEmpty;

/**
 * Base parameters class for manipulations with a list of {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfSourceListParameters extends AbstractParameters {

    private static final long serialVersionUID = 7613603633574140437L;

    @NotEmpty
    @Valid
    private List<PdfSource> sourceList;

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    public void addSource(PdfSource input) {
        if (sourceList == null) {
            sourceList = new ArrayList<PdfSource>();
        }
        sourceList.add(input);
    }

    /**
     * @return an unmodifiable view of the source list
     */
    public List<PdfSource> getSourceList() {
        return Collections.unmodifiableList(sourceList);
    }

    /**
     * Clear the source list
     */
    public void clearSourceList() {
        if (sourceList != null) {
            sourceList.clear();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(sourceList).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PdfSourceListParameters)) {
            return false;
        }
        PdfSourceListParameters parameter = (PdfSourceListParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(sourceList, parameter.getSourceList())
                .isEquals();
    }
}
