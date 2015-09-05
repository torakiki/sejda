/*
 * Created on 28/nov/2010
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

import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.validation.constraint.ValidPdfVersion;

/**
 * Validates an AbstractParameters instance ensuring that the PdfVersion set is valid considering the other fields value.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfVersionValidator implements ConstraintValidator<ValidPdfVersion, AbstractPdfOutputParameters> {

    @Override
    public void initialize(ValidPdfVersion constraintAnnotation) {
        // on purpose
    }

    @Override
    public boolean isValid(AbstractPdfOutputParameters value, ConstraintValidatorContext context) {
        boolean isValid = value == null || value.getVersion() == null
                || value.getVersion().compareTo(value.getMinRequiredPdfVersion()) >= 0;
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Invalid version %s. Minimum version required is %s.", value.getVersion(),
                            value.getMinRequiredPdfVersion())).addNode("parameters").addConstraintViolation();
        }
        return isValid;
    }

}
