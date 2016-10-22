/*
 * Created on 22 ott 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.validation.validator;

import java.awt.geom.Dimension2D;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.validation.constraint.PositiveDimensions;

/**
 * Validates a {@link Dimension2D} that should have positive height and width
 * 
 * @author Andrea Vacondio
 *
 */
public class PositiveDimensionValidator implements ConstraintValidator<PositiveDimensions, Dimension2D> {

    @Override
    public void initialize(PositiveDimensions constraintAnnotation) {
        // nothing to do
    }

    @Override
    public boolean isValid(Dimension2D value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.getWidth() > 0 && value.getHeight() > 0;
        }
        return true;
    }
}
