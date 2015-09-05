/*
 * Created on 02/ott/2011
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

import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.validation.constraint.HasSelectedPages;

/**
 * Validator for an {@link ExtractPagesParameters} to make sure that a predefined set of pages or some page selection has been set.
 * 
 * @author Andrea Vacondio
 * 
 */
public class HasSelectedPagesValidator implements ConstraintValidator<HasSelectedPages, ExtractPagesParameters> {

    @Override
    public void initialize(HasSelectedPages constraintAnnotation) {
        // nothing to do
    }

    @Override
    public boolean isValid(ExtractPagesParameters value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.getPredefinedSetOfPages() != PredefinedSetOfPages.NONE || !value.getPageSelection().isEmpty();
        }
        return true;
    }

}
