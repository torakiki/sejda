/*
 * Created on 23/ago/2011
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

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.indexOfExtension;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.output.TaskOutputDispatcher;
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

    public void initialize(SingleOutputAllowedExtensions constraintAnnotation) {
        extensions = constraintAnnotation.extensions();
    }

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

        public void dispatch(FileTaskOutput output) {
            this.outputName = output.getDestination().getName();
        }

        public void dispatch(DirectoryTaskOutput output) {
            // do nothing
        }

        public void dispatch(StreamTaskOutput output) {
            // do nothing
        }

        String getOutputName() {
            return outputName;
        }

    }
}
