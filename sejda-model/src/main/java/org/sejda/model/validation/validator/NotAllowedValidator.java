/*
 * Created on 27/dic/2012
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

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.validation.constraint.NotAllowed;

/**
 * Validator for an {@link Enum} annotated field or method where some of the values are not allowed.
 * 
 * @author Andrea Vacondio
 * 
 */
public class NotAllowedValidator implements ConstraintValidator<NotAllowed, PredefinedSetOfPages> {

    private PredefinedSetOfPages[] disallow;

    public void initialize(NotAllowed constraintAnnotation) {
        disallow = constraintAnnotation.disallow();
    }

    public boolean isValid(PredefinedSetOfPages value, ConstraintValidatorContext context) {
        if (value != null && disallow != null) {
            return !Arrays.asList(disallow).contains(value);
        }
        return true;
    }

}
