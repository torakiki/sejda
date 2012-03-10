/*
 * Created on 12/ago/2011
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
package org.sejda.model.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.output.TaskOutputDispatcher;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.ValidSingleOutput;

/**
 * Validates that a single output parameter has a valid output name if the selected output is not a file. The output name is used when writing the generated output to a zip stream
 * or a directory.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SingleOutputValidator implements ConstraintValidator<ValidSingleOutput, SingleOutputTaskParameters> {

    public void initialize(ValidSingleOutput constraintAnnotation) {
        // nothing to do
    }

    public boolean isValid(SingleOutputTaskParameters value, ConstraintValidatorContext context) {
        if (value != null && value.getOutput() != null) {
            return isValidOutputType(value);
        }
        return true;
    }

    private boolean isValidOutputType(SingleOutputTaskParameters value) {
        OutputNameValidatorDispatcher dispatcher = new OutputNameValidatorDispatcher(value.getOutputName());
        try {
            value.getOutput().accept(dispatcher);
        } catch (TaskException e) {
            // should never happen
            throw new SejdaRuntimeException(e);
        }
        return dispatcher.isValid();
    }

    /**
     * Dispatcher to validate according to the runtime type of the task output.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class OutputNameValidatorDispatcher implements TaskOutputDispatcher {

        private boolean valid = true;
        private String outputName;

        OutputNameValidatorDispatcher(String outputName) {
            this.outputName = outputName;
        }

        @Override
        public void dispatch(FileTaskOutput output) {
            this.valid = true;
        }

        @Override
        public void dispatch(DirectoryTaskOutput output) {
            this.valid = StringUtils.isNotBlank(outputName);

        }

        @Override
        public void dispatch(StreamTaskOutput output) {
            this.valid = StringUtils.isNotBlank(outputName);
        }

        boolean isValid() {
            return valid;
        }

    }

}
