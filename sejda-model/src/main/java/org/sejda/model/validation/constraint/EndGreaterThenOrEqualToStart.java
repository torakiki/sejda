/*
 * Created on 26/giu/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import org.sejda.model.validation.validator.PageRangeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint validating that a page range instance is a valid, start page cannot be greater then end page.
 * 
 * @author Andrea Vacondio
 * 
 */
@NotNull
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PageRangeValidator.class)
@Documented
public @interface EndGreaterThenOrEqualToStart {

    String message() default "Invalid page range,ends before starting.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
