/*
 * Created on 27/nov/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.input;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
    private PdfSource<?> source;
    private boolean reverse = false;
    @Min(value = 1)
    private int step = 1;

    PdfMixInput() {
        // default constructor for persistence
    }

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


    @Override
    public String toString() {
        return new ToStringBuilder(this).append(source).append(reverse).append(step).toString();
    }
}
