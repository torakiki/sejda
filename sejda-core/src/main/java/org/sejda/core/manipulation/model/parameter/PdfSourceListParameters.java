/*
 * Created on 17/set/2010
 *
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

    @NotEmpty
    @Valid
    private List<PdfSource> sourceList = new ArrayList<PdfSource>();

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    public void addSource(PdfSource input) {
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
        sourceList.clear();
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
        if (!(other instanceof PdfSourceListParameters)) {
            return false;
        }
        PdfSourceListParameters parameter = (PdfSourceListParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(sourceList, parameter.getSourceList())
                .isEquals();
    }
}
