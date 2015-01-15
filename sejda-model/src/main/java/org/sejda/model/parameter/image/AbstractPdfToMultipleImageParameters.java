/*
 * Created on 18/set/2011
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
package org.sejda.model.parameter.image;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Base class for a parameter meant to convert an existing pdf source to multiple images of a specified type.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractPdfToMultipleImageParameters extends AbstractPdfToImageParameters implements
        MultipleOutputTaskParameters, PageRangeSelection, PagesSelection {

    AbstractPdfToMultipleImageParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

    private String outputPrefix = "";
    @Valid
    @NotNull
    private MultipleTaskOutput<?> output;

    @Valid
    private final Set<PageRange> pageSelection = new NullSafeSet<PageRange>();

    public void addPageRange(PageRange range) {
        pageSelection.add(range);
    }

    public void addAllPageRanges(Collection<PageRange> ranges) {
        pageSelection.addAll(ranges);
    }

    /**
     * @return an unmodifiable view of the pageSelection
     */
    public Set<PageRange> getPageSelection() {
        return Collections.unmodifiableSet(pageSelection);
    }

    /**
     * @param upperLimit
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages. Iteration ordering is predictable, it is the order in which elements were inserted into the {@link PageRange} set or the natural order in
     *         case of all pages.
     * @see org.sejda.model.pdf.page.PagesSelection#getPages(int)
     */
    public Set<Integer> getPages(int upperLimit) {
        if (pageSelection.isEmpty()) {
            return PredefinedSetOfPages.ALL_PAGES.getPages(upperLimit);
        }

        Set<Integer> retSet = new NullSafeSet<Integer>();
        for (PageRange range : getPageSelection()) {
            retSet.addAll(range.getPages(upperLimit));
        }
        return retSet;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public MultipleTaskOutput<?> getOutput() {
        return output;
    }

    public void setOutput(MultipleTaskOutput<?> output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).append(outputPrefix).append(pageSelection).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfToMultipleImageParameters)) {
            return false;
        }
        AbstractPdfToMultipleImageParameters parameter = (AbstractPdfToMultipleImageParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(output, parameter.getOutput())
                .append(outputPrefix, parameter.getOutputPrefix()).append(pageSelection, parameter.pageSelection).isEquals();
    }
}
