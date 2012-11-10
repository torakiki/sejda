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
package org.sejda.model.parameter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.footer.PdfFooterLabel;
import org.sejda.model.validation.constraint.NotEmpty;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Parameters configuring how to label the footer of pages
 * 
 * @author Eduard Weissmann
 * 
 */
@SingleOutputAllowedExtensions
public class SetFooterParameters extends SinglePdfSourceSingleOutputParameters {

    @NotEmpty
    @Valid
    private final Map<Integer, PdfFooterLabel> labels = new HashMap<Integer, PdfFooterLabel>();

    /**
     * Apply label for all pages starting with pageNumber
     * @return previous label associated with pageNumber starting point
     */
    public PdfFooterLabel putLabel(int pageNumber, PdfFooterLabel label) {
        return this.labels.put(pageNumber, label);
    }

    /**
     * @return an unmodifiable view of the labels in this parameter.
     */
    public Map<Integer, PdfFooterLabel> getLabels() {
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
        if (!(other instanceof SetFooterParameters)) {
            return false;
        }
        SetFooterParameters parameter = (SetFooterParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(getLabels(), parameter.getLabels())
                .isEquals();
    }

    /**
     * Return the footer label to be applied to a pdf page number
     */
    public String formatLabelFor(int pageNumber) {
        int labelDefStartPage = getLabelDefinitionStartPageFor(pageNumber);
        if(labelDefStartPage <= 0) {
            return null;
        }

        int offset = pageNumber - labelDefStartPage;
        PdfFooterLabel label = getLabels().get(labelDefStartPage);
        return label.formatFor(offset);
    }


    /**
     * Find a page number x, for starting with which, the user defined a label that should be applied also to input pageNumber
     * Eg: user defines label1 for pages starting at 10 and label2 for pages starting with 100. key page for 12 would be 1, key page for 101 would be 100, key page for 9 would be 0
     */
    private int getLabelDefinitionStartPageFor(int pageNumber) {
        if(pageNumber <= 0) {
            return pageNumber;
        }

        if(labels.containsKey(pageNumber)){
            return pageNumber;
        } else {
            return getLabelDefinitionStartPageFor(findHighestStartPageLowerThan(pageNumber - 1));
        }
    }

    private int findHighestStartPageLowerThan(int page) {
        int prevStartPage = 0;
        for(int startPage : new TreeSet<Integer>(labels.keySet())) {
            if(startPage > page) {
                return prevStartPage;
            }

            prevStartPage = startPage;
        }

        return prevStartPage;
    }

}
