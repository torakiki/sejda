/*
 * Created on 12/ago/2011
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.ValidSingleOutput;

/**
 * Validates that a single output parameter has a valid output name if the selected output is not a file. The output name is used when writing the generated output to a zip stream
 * or a directory.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SingleOutputValidator implements ConstraintValidator<ValidSingleOutput, SingleOutputTaskParameters> {

    @Override
    public void initialize(ValidSingleOutput constraintAnnotation) {
        // nothing to do
    }

    @Override
    public boolean isValid(SingleOutputTaskParameters value, ConstraintValidatorContext context) {
        if (nonNull(value)) {
            if (isNull(value.getOutput())) {
                return false;
            }
            if (isNull(value.getOutput().getDestination()) || (value.getOutput().getDestination().exists()
                    && (value.getExistingOutputPolicy() == ExistingOutputPolicy.FAIL
                            || value.getExistingOutputPolicy() == ExistingOutputPolicy.SKIP))) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        String.format("File destination already exists: %s.", value.getOutput().getDestination()))
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

}
