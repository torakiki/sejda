/*
 * Created on 10/set/2011
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

import org.sejda.core.manipulation.model.RectangularBox;
import org.sejda.core.validation.constraint.ValidCoordinates;

/**
 * Validator ensuring that, given a {@link RectangularBox}, top is greater then bottom and right is greater then left.
 * 
 * @author Andrea Vacondio
 * 
 */
public class CoordinatesValidator implements ConstraintValidator<ValidCoordinates, RectangularBox> {

    public void initialize(ValidCoordinates constraintAnnotation) {
        // nothing to do
    }

    public boolean isValid(RectangularBox value, ConstraintValidatorContext context) {
        if (value != null) {
            if (value.getTop() <= value.getBottom() || value.getRight() <= value.getLeft()) {
                return false;
            }
        }
        return true;
    }

}
