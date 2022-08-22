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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.commons.collection.NullSafeSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Parameter class for a split by page task. Used to perform split at a given set of page numbers.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByPagesParameters extends AbstractSplitByPageParameters {

    @NotEmpty
    private final Set<Integer> pages = new NullSafeSet<Integer>();

    /**
     * Adds all pages to split at.
     * 
     * @param pagesToAdd
     */
    public void addPages(Collection<Integer> pagesToAdd) {
        pages.addAll(pagesToAdd);
    }

    /**
     * Adds a page to split at.
     * 
     * @param page
     */
    public void addPage(Integer page) {
        pages.add(page);
    }

    @Override
    public Set<Integer> getPages(int upperLimit) {
        return Collections
                .unmodifiableSet(pages.stream().filter(p -> p <= upperLimit && p > 0).collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(pages).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(pages).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitByPagesParameters)) {
            return false;
        }
        SplitByPagesParameters parameter = (SplitByPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(pages, parameter.pages).isEquals();
    }
}
