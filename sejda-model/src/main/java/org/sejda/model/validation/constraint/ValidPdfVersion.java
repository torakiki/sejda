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
package org.sejda.model.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;

import org.sejda.model.validation.validator.PdfVersionValidator;

/**
 * Constraint validating that the pdf version specified in an {@link org.sejda.model.parameter.base.AbstractPdfOutputParameters} is compatible with the other class attributes.
 * 
 * @author Andrea Vacondio
 * 
 */
@NotNull
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = { PdfVersionValidator.class })
@Documented
@ReportAsSingleViolation
public @interface ValidPdfVersion {

    String message() default "The minimum pdf version required is higher then the selected one.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
