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
package org.sejda.core.validation.validator;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.indexOfExtension;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.ArrayUtils;
import org.sejda.core.manipulation.model.output.FileOutput;
import org.sejda.core.manipulation.model.output.OutputType;
import org.sejda.core.manipulation.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.core.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Validates that the input single output task parameter has a {@link FileOutput} whose file is of the expected type (extension) or, if not a {@link FileOutput}, the outputName is
 * of the expected type (extension).
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
        if (value.getOutput().getOutputType() == OutputType.FILE_OUTPUT) {
            File outputFile = ((FileOutput) value.getOutput()).getFile();
            if (outputFile != null) {
                return outputFile.getName();
            }
        } else {
            return value.getOutputName();
        }
        return "";
    }
}
