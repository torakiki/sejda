/*
 * Created on 28/nov/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;

import org.sejda.core.validation.validator.PdfVersionValidator;

/**
 * Constraint validating that the pdf version specified in an {@link org.sejda.core.manipulation.model.parameter.base.AbstractPdfOutputParameters} is compatible with the other class attributes.
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
