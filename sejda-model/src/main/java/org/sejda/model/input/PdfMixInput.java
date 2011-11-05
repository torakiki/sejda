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
package org.sejda.model.input;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model representation of a input parameter for the Alternate Mix task. Contains a {@link AbstractPdfSource}, the mix step and a parameter indicating if the document should be
 * processed in reverse mode.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfMixInput {

    @NotNull
    @Valid
    private final PdfSource<?> source;
    private boolean reverse = false;
    @Min(value = 1)
    private int step = 1;

    public PdfMixInput(PdfSource<?> source, boolean reverse, int step) {
        this.source = source;
        this.reverse = reverse;
        this.step = step;
    }

    /**
     * Creates an instance with <tt>step</tt> of 1 <tt>reverse</tt> false
     * 
     * @param source
     */
    public PdfMixInput(PdfSource<?> source) {
        this.source = source;
    }

    public PdfSource<?> getSource() {
        return source;
    }

    public boolean isReverse() {
        return reverse;
    }

    public int getStep() {
        return step;
    }

    /**
     * @param numberOfPages
     *            the number of pages for this input.
     * @return a new mix processing status for this input
     */
    public PdfMixInputProcessStatus newProcessingStatus(int numberOfPages) {
        return new PdfMixInputProcessStatus(numberOfPages);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(source).append(reverse).append(step).toString();
    }

    /**
     * Holds the status of the process for the enclosing a {@link PdfMixInput}
     * 
     * @author Andrea Vacondio
     * 
     */
    public final class PdfMixInputProcessStatus {

        private int currentPage;
        private int numberOfPages;

        private PdfMixInputProcessStatus(int numberOfPages) {
            this.numberOfPages = numberOfPages;
            this.currentPage = (reverse) ? numberOfPages : 1;
        }

        /**
         * @return the next page number
         */
        public int nextPage() {
            int retVal = currentPage;
            if (reverse) {
                currentPage--;
            } else {
                currentPage++;
            }
            return retVal;
        }

        /**
         * @return true if there is another page to be processed
         */
        public boolean hasNextPage() {
            return (currentPage > 0 && currentPage <= numberOfPages);
        }
    }
}
