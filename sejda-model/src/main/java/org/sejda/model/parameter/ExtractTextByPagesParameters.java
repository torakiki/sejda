/*
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.parameter;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.model.validation.constraint.NotEmpty;

/**
 * Parameters for extract text by pages manipulation. Accepts a set of page numbers, where the split occurs.
 * Works similar to SplitByPages, with the difference that the output will be extracted text files.
 *
 * @author Edi Weissmann
 */
public class ExtractTextByPagesParameters extends SinglePdfSourceMultipleOutputParameters {

    @NotEmpty
    private final Set<Integer> pages = new NullSafeSet<Integer>();
    private String textEncoding;

    public String getTextEncoding() {
        return textEncoding;
    }

    public void setTextEncoding(String textEncoding) {
        this.textEncoding = textEncoding;
    }

    public void addPages(Collection<Integer> pagesToAdd) {
        pages.addAll(pagesToAdd);
    }

    public void addPage(Integer page) {
        pages.add(page);
    }

    public void setPages(Set<Integer> pages) {
        this.pages.clear();
        this.pages.addAll(pages);
    }

    public Set<Integer> getPages(int upperLimit) {
        Set<Integer> filteredSet = new TreeSet<Integer>();
        for (Integer page : pages) {
            if (page != null && page <= upperLimit && page > 0) {
                filteredSet.add(page);
            }
        }
        return filteredSet;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(pages).append(textEncoding)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExtractTextByPagesParameters)) {
            return false;
        }
        ExtractTextByPagesParameters parameter = (ExtractTextByPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(pages, parameter.pages)
                .append(textEncoding, parameter.getTextEncoding()).isEquals();
    }

}
