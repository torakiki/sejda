/*
 * Created on 23/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.sejda.core.Sejda;
import org.sejda.core.validation.validator.SingleOutputExtensionsValidator;

/**
 * Constraint validating that a single output parameter has one of the expected output extensions (in the output file name for file task output or in the outputname attribute for
 * non file task output). Default expected extension is Pdf.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidSingleOutput
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = SingleOutputExtensionsValidator.class)
@Documented
public @interface SingleOutputAllowedExtensions {
    String message() default "TaskOutput is not of the expected type.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] extensions() default { Sejda.PDF_EXTENSION };
}
