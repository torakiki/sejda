/*
 * Created on 10/ago/2011
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
package org.sejda.core.manipulation.model.input;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.manipulation.model.pdf.page.PageRange;
import org.sejda.core.validation.constraint.NoIntersections;

/**
 * Model for a input source for a merge task. It contains the source and the page selection on the source.
 * 
 * @author Andrea Vacondio
 * 
 */
@NoIntersections
public class PdfMergeInput {

    @NotNull
    @Valid
    private final PdfSource source;
    @Valid
    private final Set<PageRange> pageSelection = new LinkedHashSet<PageRange>();

    public PdfMergeInput(PdfSource source) {
        this.source = source;
    }

    public PdfSource getSource() {
        return source;
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    public void addPageRange(PageRange range) {
        pageSelection.add(range);
    }

    /**
     * @return true if page selection for this input contains all the pages of the input source.
     */
    public boolean isAllPages() {
        return pageSelection.isEmpty();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("source", source).append("pageSelection", pageSelection).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(source).append(pageSelection).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PdfMergeInput)) {
            return false;
        }
        PdfMergeInput input = (PdfMergeInput) other;
        return new EqualsBuilder().append(source, input.getSource()).append(pageSelection, input.pageSelection)
                .isEquals();
    }
}
