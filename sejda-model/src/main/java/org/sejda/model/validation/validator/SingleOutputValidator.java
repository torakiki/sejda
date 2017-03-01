/*
 * Created on 12/ago/2011
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
package org.sejda.model.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.*;
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

    @Override
    public void initialize(ValidSingleOutput constraintAnnotation) {
        // nothing to do
    }

    @Override
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
        public void dispatch(FileOrDirectoryTaskOutput output) {
            if(output.getDestination().isDirectory()) {
                this.valid = StringUtils.isNotBlank(outputName);
            } else {
                this.valid = true;
            }
        }

        boolean isValid() {
            return valid;
        }

    }

}
