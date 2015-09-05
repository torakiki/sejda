/*
 * Created on 26/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.validation.constraint.EndGreaterThenOrEqualToStart;

/**
 * Validates that in a {@link PageRange} the end page is greater or equals the start page.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageRangeValidator implements ConstraintValidator<EndGreaterThenOrEqualToStart, PageRange> {

    @Override
    public void initialize(EndGreaterThenOrEqualToStart constraintAnnotation) {
        // on purpose
    }

    @Override
    public boolean isValid(PageRange value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.getStart() <= value.getEnd();
        }
        return true;
    }

}
