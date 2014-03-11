/*
 * Created on 06/mar/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.validation.constraint.HasAPassword;

/**
 * Validates that an owner password or a user password has been specified. The empty string is a valid value
 * 
 * @author Andrea Vacondio
 * 
 */
public class HasAPasswordValidator implements ConstraintValidator<HasAPassword, EncryptParameters> {
    public void initialize(HasAPassword constraintAnnotation) {
        // on purpose
    }

    public boolean isValid(EncryptParameters value, ConstraintValidatorContext context) {
        if (value != null) {
            return isNotBlank(value.getOwnerPassword()) || isNotBlank(value.getUserPassword());
        }
        return true;
    }
}
