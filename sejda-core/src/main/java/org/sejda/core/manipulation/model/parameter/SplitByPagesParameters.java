/*
 * Created on 03/ago/2011
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
package org.sejda.core.manipulation.model.parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.support.NullSafeSet;
import org.sejda.core.validation.constraint.NotEmpty;

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
        Set<Integer> filteredSet = new HashSet<Integer>();
        for (Integer page : pages) {
            if (page != null && page <= upperLimit && page > 0) {
                filteredSet.add(page);
            }
        }
        return Collections.unmodifiableSet(filteredSet);
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
