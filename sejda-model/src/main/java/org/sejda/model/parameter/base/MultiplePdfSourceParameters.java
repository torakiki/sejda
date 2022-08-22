/*
 * Created on 17/set/2010
 *
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
package org.sejda.model.parameter.base;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base parameters class for manipulations with a list of {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class MultiplePdfSourceParameters extends AbstractPdfOutputParameters implements
        MultiplePdfSourceTaskParameters {

    @NotEmpty
    @Valid
    private final List<PdfSource<?>> sourceList = new ArrayList<>();

    public void addSources(Collection<PdfSource<?>> inputs) {
        sourceList.addAll(inputs);
    }

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    @Override
    public void addSource(PdfSource<?> input) {
        sourceList.add(input);
    }

    public void removeAllSources() {
        sourceList.clear();
    }

    /**
     * @return an unmodifiable view of the source list
     */
    @Override
    public List<PdfSource<?>> getSourceList() {
        return Collections.unmodifiableList(sourceList);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(sourceList).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MultiplePdfSourceParameters)) {
            return false;
        }
        MultiplePdfSourceParameters parameter = (MultiplePdfSourceParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(sourceList, parameter.getSourceList())
                .isEquals();
    }
}
