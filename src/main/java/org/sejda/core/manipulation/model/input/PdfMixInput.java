/*
 * Created on 27/nov/2010
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
package org.sejda.core.manipulation.model.input;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model representation of a input parameter for the Alternate Mix task. Contains a {@link PdfSource}, the mix step and a parameter indicating if the document should be processed
 * in reverse mode.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfMixInput {

    @NotNull
    @Valid
    private final PdfSource source;
    private boolean reverse = false;
    @Min(value = 1)
    private int step = 1;

    public PdfMixInput(PdfSource source, boolean reverse, int step) {
        this.source = source;
        this.reverse = reverse;
        this.step = step;
    }

    /**
     * Creates an instance with <tt>step</tt> of 1 <tt>reverse</tt> false
     * 
     * @param source
     */
    public PdfMixInput(PdfSource source) {
        this.source = source;
    }

    public PdfSource getSource() {
        return source;
    }

    public boolean isReverse() {
        return reverse;
    }

    public int getStep() {
        return step;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(source).append(reverse).append(step).toString();
    }
}
