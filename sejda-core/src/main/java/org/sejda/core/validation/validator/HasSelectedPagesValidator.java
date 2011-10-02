/*
 * Created on 02/ott/2011
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.core.manipulation.model.parameter.ExtractPagesParameters;
import org.sejda.core.validation.constraint.HasSelectedPages;

/**
 * Validator for an {@link ExtractPagesParameters} to make sure that a predefined set of pages or some page selection has been set.
 * 
 * @author Andrea Vacondio
 * 
 */
public class HasSelectedPagesValidator implements ConstraintValidator<HasSelectedPages, ExtractPagesParameters> {

    public void initialize(HasSelectedPages constraintAnnotation) {
        // nothing to do
    }

    public boolean isValid(ExtractPagesParameters value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.getPredefinedSetOfPages() != null || !value.getPageSelection().isEmpty();
        }
        return true;
    }

}
