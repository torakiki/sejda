/*
 * Created on 27/dic/2012
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

    @Override
    public void initialize(NotAllowed constraintAnnotation) {
        disallow = constraintAnnotation.disallow();
    }

    @Override
    public boolean isValid(PredefinedSetOfPages value, ConstraintValidatorContext context) {
        if (value != null && disallow != null) {
            return !Arrays.asList(disallow).contains(value);
        }
        return true;
    }

}
