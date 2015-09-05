/*
 * Created on 06/mar/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.validation.constraint.HasAPassword;

/**
 * Validates that an owner password or a user password has been specified. The empty string is a valid value
 * 
 * @author Andrea Vacondio
 * 
 */
public class HasAPasswordValidator implements ConstraintValidator<HasAPassword, EncryptParameters> {
    @Override
    public void initialize(HasAPassword constraintAnnotation) {
        // on purpose
    }

    @Override
    public boolean isValid(EncryptParameters value, ConstraintValidatorContext context) {
        if (value != null) {
            return isNotBlank(value.getOwnerPassword()) || isNotBlank(value.getUserPassword());
        }
        return true;
    }
}
