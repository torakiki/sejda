/*
 * Created on 26/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    @Override
    public void initialize(Positive minValue) {
        // on purpose
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 1;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).compareTo(BigInteger.ZERO) == 1;
        } else {
            return value.floatValue() > 0;
        }
    }
}