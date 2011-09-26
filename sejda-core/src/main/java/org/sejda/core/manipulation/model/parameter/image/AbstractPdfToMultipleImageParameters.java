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
package org.sejda.core.manipulation.model.parameter.image;

import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.image.ImageColorType;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.output.TaskOutput;
import org.sejda.core.manipulation.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.core.validation.constraint.TaskOutputAllowedTypes;

/**
 * Base class for a parameter meant to convert an existing pdf source to multiple images of a specified type.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractPdfToMultipleImageParameters extends AbstractPdfToImageParameters implements
        MultipleOutputTaskParameters {

    AbstractPdfToMultipleImageParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

    private String outputPrefix = "";
    @Valid
    @TaskOutputAllowedTypes(values = { OutputType.DIRECTORY_OUTPUT, OutputType.STREAM_OUTPUT })
    private TaskOutput output;

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public TaskOutput getOutput() {
        return output;
    }

    public void setOutput(TaskOutput output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(output).append(outputPrefix).toHashCode();
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
                .append(outputPrefix, parameter.getOutputPrefix()).isEquals();
    }
}
