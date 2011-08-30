/*
 * Created on 24/giu/2010
 *
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
package org.sejda.core.validation.validator;

import static org.apache.commons.io.FilenameUtils.indexOfExtension;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.io.FilenameUtils;
import org.sejda.core.validation.constraint.FileExtension;

/**
 * Validates the extension of the a file
 * 
 * @author Andrea Vacondio
 * 
 */
public class FileExtensionValidator implements ConstraintValidator<FileExtension, File> {

    private String expectedExtension;

    public void initialize(FileExtension constraintAnnotation) {
        expectedExtension = constraintAnnotation.value();
    }

    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null && value.isFile()) {
            String extension = FilenameUtils.getExtension(value.getName());
            return equalsIgnoreCase(expectedExtension, extension) && indexOfExtension(value.getName()) > 0;
        }
        return true;
    }

}
