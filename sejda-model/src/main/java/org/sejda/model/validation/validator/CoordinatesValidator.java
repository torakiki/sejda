/*
 * Created on 10/set/2011
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.RectangularBox;
import org.sejda.model.validation.constraint.ValidCoordinates;

/**
 * Validator ensuring that, given a {@link RectangularBox}, top is greater then bottom and right is greater then left.
 * 
 * @author Andrea Vacondio
 * 
 */
public class CoordinatesValidator implements ConstraintValidator<ValidCoordinates, RectangularBox> {

    @Override
    public void initialize(ValidCoordinates constraintAnnotation) {
        // nothing to do
    }

    @Override
    public boolean isValid(RectangularBox value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.getTop() > value.getBottom() && value.getRight() > value.getLeft();
        }
        return true;
    }

}
