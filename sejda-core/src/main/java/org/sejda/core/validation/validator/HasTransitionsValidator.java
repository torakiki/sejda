/*
 * Created on 03/set/2011
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

import org.sejda.core.manipulation.model.parameter.SetPagesTransitionParameters;
import org.sejda.core.validation.constraint.HasTransitions;

/**
 * Makes sure that at least one trasition is set. Validator to validate a {@link SetPagesTransitionParameters} to make sure it has a default transition or a not empty set of
 * transitions.
 * 
 * @author Andrea Vacondio
 * 
 */
public class HasTransitionsValidator implements ConstraintValidator<HasTransitions, SetPagesTransitionParameters> {

    public void initialize(HasTransitions constraintAnnotation) {
        // nothing to do
    }

    public boolean isValid(SetPagesTransitionParameters value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.getDefaultTransition() != null || !value.getTransitions().isEmpty();
        }
        return true;
    }

}
