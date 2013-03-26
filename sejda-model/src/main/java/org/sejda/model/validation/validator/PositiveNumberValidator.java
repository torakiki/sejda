/*
 * Created on 26/mar/2013
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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.validation.constraint.Positive;

/**
 * Validates a number that should be positive.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PositiveNumberValidator implements ConstraintValidator<Positive, Number> {

    public void initialize(Positive minValue) {
        // on purpose
    }

    public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 1;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).compareTo(BigInteger.ZERO) == 1;
        } else {
            return value.longValue() > 0;
        }
    }
}