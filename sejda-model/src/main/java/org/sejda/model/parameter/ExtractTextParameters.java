/*
 * Created on 24/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.parameter.base.AbstractParameters;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.parameter.base.MultiplePdfSourceTaskParameters;
import org.sejda.model.validation.constraint.NotEmpty;

/**
 * Parameter class for the extract text manipulation. Accepts a list of {@link PdfSource} from which the text will be extracted.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExtractTextParameters extends AbstractParameters
        implements MultiplePdfSourceTaskParameters, MultipleOutputTaskParameters {

    private String outputPrefix = "";
    @Valid
    @NotNull
    private MultipleTaskOutput<?> output;
    @NotEmpty
    @Valid
    private List<PdfSource<?>> sourceList = new ArrayList<PdfSource<?>>();
    @NotEmpty
    private String textEncoding = "UTF-8";

    public String getTextEncoding() {
        return textEncoding;
    }

    public void setTextEncoding(String textEncoding) {
        this.textEncoding = textEncoding;
    }

    @Override
    public MultipleTaskOutput<?> getOutput() {
        return output;
    }

    @Override
    public void setOutput(MultipleTaskOutput<?> output) {
        this.output = output;
    }

    @Override
    public String getOutputPrefix() {
        return outputPrefix;
    }

    @Override
    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    @Override
    public void addSource(PdfSource<?> input) {
        sourceList.add(input);
    }

    /**
     * @return an unmodifiable view of the source list
     */
    @Override
    public List<PdfSource<?>> getSourceList() {
        return Collections.unmodifiableList(sourceList);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).append(sourceList)
                .append(textEncoding).append(outputPrefix).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExtractTextParameters)) {
            return false;
        }
        ExtractTextParameters parameter = (ExtractTextParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(output, parameter.output)
                .append(sourceList, parameter.sourceList).append(textEncoding, parameter.textEncoding)
                .append(outputPrefix, parameter.outputPrefix).isEquals();
    }

}
