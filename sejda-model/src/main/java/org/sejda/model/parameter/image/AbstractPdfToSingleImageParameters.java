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
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.ValidSingleOutput;

/**
 * Base class for a parameter meant to convert an existing pdf source to a single image of a specified type. The type must support multiple images into one image file.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidSingleOutput
public abstract class AbstractPdfToSingleImageParameters extends AbstractPdfToImageParameters implements
        SingleOutputTaskParameters {

    AbstractPdfToSingleImageParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

    private String outputName;
    @Valid
    @NotNull
    private TaskOutput output;

    public String getOutputName() {
        return outputName;
    }

    public TaskOutput getOutput() {
        return output;
    }

    public void setOutput(TaskOutput output) {
        this.output = output;
    }

    /**
     * @param outputName
     *            the outputName to be used when the output is not a file destination
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputName).append(output).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfToSingleImageParameters)) {
            return false;
        }
        AbstractPdfToSingleImageParameters parameter = (AbstractPdfToSingleImageParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputName, parameter.getOutputName())
                .append(output, parameter.getOutput()).isEquals();
    }

}
