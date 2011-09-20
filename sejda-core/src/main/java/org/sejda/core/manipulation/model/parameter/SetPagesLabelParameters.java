/*
 * Created on 23/gen/2011
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.core.manipulation.model.pdf.label.PdfPageLabel;
import org.sejda.core.validation.constraint.NotEmpty;
import org.sejda.core.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for the set pages label manipulation. The manipulation will apply a label to physical pages until a new label for a physical page number is found.
 * <p>
 * Ex. if the parameters contain two labels, "label1" for physical page 1 and "label2" for physical page 5, the resulting document will have pages 1 to 4 where "label1" is applied
 * and pages 5 till the end of the document where "label2" is applied.
 * 
 * @author Andrea Vacondio
 * 
 */
@SingleOutputAllowedExtensions
public class SetPagesLabelParameters extends SinglePdfSourceSingleOutputParameters {

    @NotEmpty
    @Valid
    private final Map<Integer, PdfPageLabel> labels = new HashMap<Integer, PdfPageLabel>();

    /**
     * Associates the given label to the given page number. If a label was already associated to the given page, it is replaced with the new one.
     * 
     * @param page
     * @param label
     * @return the previously associated label or null.
     */
    public PdfPageLabel putLabel(Integer page, PdfPageLabel label) {
        return labels.put(page, label);
    }

    /**
     * @return an unmodifiable view of the labels in this parameter.
     */
    public Map<Integer, PdfPageLabel> getLabels() {
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
        if (!(other instanceof SetPagesLabelParameters)) {
            return false;
        }
        SetPagesLabelParameters parameter = (SetPagesLabelParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(getLabels(), parameter.getLabels())
                .isEquals();
    }

}
