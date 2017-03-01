/*
 * Created on 23/ago/2011
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

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.indexOfExtension;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.*;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Validates that the input single output task parameter has a {@link FileTaskOutput} whose file is of the expected type (extension) or, if not a {@link FileTaskOutput}, the
 * outputName is of the expected type (extension).
 * 
 * @author Andrea Vacondio
 * 
 */
public class SingleOutputExtensionsValidator implements
        ConstraintValidator<SingleOutputAllowedExtensions, SingleOutputTaskParameters> {

    private String[] extensions;

    @Override
    public void initialize(SingleOutputAllowedExtensions constraintAnnotation) {
        extensions = constraintAnnotation.extensions();
    }

    @Override
    public boolean isValid(SingleOutputTaskParameters value, ConstraintValidatorContext context) {
        if (value != null && value.getOutput() != null && ArrayUtils.isNotEmpty(extensions)) {
            String fileName = getOutputFileName(value);

            if (hasAllowedExtension(fileName)) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("The output '%s' is not one of the expected types: %s", fileName,
                            ArrayUtils.toString(extensions))).addNode("taskOutput").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean hasAllowedExtension(String fileName) {
        String extension = getExtension(fileName);
        for (String current : extensions) {
            if (equalsIgnoreCase(current, extension) && indexOfExtension(fileName) > 0) {
                return true;
            }
        }
        return false;
    }

    private String getOutputFileName(SingleOutputTaskParameters value) {
        NameRetriever retriever = new NameRetriever(value.getOutputName());
        try {
            value.getOutput().accept(retriever);
        } catch (TaskException e) {
            // should never happen
            throw new SejdaRuntimeException(e);
        }
        return retriever.getOutputName();
    }

    /**
     * Retrieves the name to validate depending on the runtime type of the task output.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class NameRetriever implements TaskOutputDispatcher {

        private String outputName;

        private NameRetriever(String outputName) {
            this.outputName = outputName;
        }

        @Override
        public void dispatch(FileTaskOutput output) {
            this.outputName = output.getDestination().getName();
        }

        @Override
        public void dispatch(DirectoryTaskOutput output) {
            // do nothing
        }

        @Override
        public void dispatch(FileOrDirectoryTaskOutput output) {
            if(output.getDestination().isDirectory()) {
                // do nothing
            } else {
                this.outputName = output.getDestination().getName();
            }
        }

        String getOutputName() {
            return outputName;
        }

    }
}
