/*
 * Created on 09/lug/2010
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

import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.core.validation.constraint.ExistingFile;

/**
 * Validates that the given file exists.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExistingFileValidator implements ConstraintValidator<ExistingFile, File> {

    public void initialize(ExistingFile constraintAnnotation) {
        // on purpose
    }

    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.exists();
        }
        return true;
    }

}
