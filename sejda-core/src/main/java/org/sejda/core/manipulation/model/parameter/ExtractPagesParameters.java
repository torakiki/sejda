/*
 * Created on 25/ago/2011
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
package org.sejda.core.manipulation.model.parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.output.TaskOutput;
import org.sejda.core.manipulation.model.pdf.page.PageRange;
import org.sejda.core.manipulation.model.pdf.page.PageRangeSelection;
import org.sejda.core.manipulation.model.pdf.page.PagesSelection;
import org.sejda.core.manipulation.model.pdf.page.PredefinedSetOfPages;
import org.sejda.core.validation.constraint.NoIntersections;
import org.sejda.core.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for an Extract pages task. Allow to specify a predefined set of pages to extract (odd, even) or a set of page ranges but not both. Page ranges are validated to
 * make sure that there is no intersection.
 * 
 * @author Andrea Vacondio
 * 
 */
@NoIntersections
@SingleOutputAllowedExtensions
// TODO validate setOfPages or at least one range are specified
public class ExtractPagesParameters extends SinglePdfSourceParameters implements SingleOutputTaskParameters,
        PageRangeSelection, PagesSelection {

    private PredefinedSetOfPages setOfPages;
    @Valid
    private final Set<PageRange> pageSelection = new LinkedHashSet<PageRange>();
    private String outputName;
    @Valid
    @NotNull
    private TaskOutput output;

    /**
     * Creates an instance using a predefined set of pages to extract.
     * 
     * @param setOfPages
     */
    public ExtractPagesParameters(PredefinedSetOfPages setOfPages) {
        this.setOfPages = setOfPages;
    }

    /**
     * Creates an instance using the input page ranges selection.
     * 
     * @param pageRanges
     */
    public ExtractPagesParameters(Collection<PageRange> pageRanges) {
        this.pageSelection.addAll(pageRanges);
    }

    public String getOutputName() {
        return outputName;
    }

    /**
     * @param outputName
     *            the outputName to be used when the output is not a file destination
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public TaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(TaskOutput output) {
        this.output = output;
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    public Set<Integer> getPages(int upperLimit) {
        if (setOfPages != null) {
            return setOfPages.getPages(upperLimit);
        }
        Set<Integer> retSet = new HashSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(setOfPages).append(pageSelection)
                .append(output).append(outputName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExtractPagesParameters)) {
            return false;
        }
        ExtractPagesParameters parameter = (ExtractPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(setOfPages, parameter.setOfPages)
                .append(pageSelection, parameter.pageSelection).append(output, parameter.getOutput())
                .append(outputName, parameter.getOutputName()).isEquals();
    }
}
