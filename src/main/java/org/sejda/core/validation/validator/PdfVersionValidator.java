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
package org.sejda.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.core.manipulation.model.parameter.AbstractParameters;
import org.sejda.core.validation.constraint.ValidPdfVersion;

/**
 * Validates an AbstractParameters instance ensuring that the PdfVersion set is valid considering the other fields value.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfVersionValidator implements ConstraintValidator<ValidPdfVersion, AbstractParameters> {

    public void initialize(ValidPdfVersion constraintAnnotation) {
        // on purpose
    }

    public boolean isValid(AbstractParameters value, ConstraintValidatorContext context) {
        boolean isValid = value.getVersion() == null
                || value.getVersion().compareTo(value.getMinRequiredPdfVersion()) >= 0;
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Invalid version %s. Minimum version required is %s.", value.getVersion(), value
                            .getMinRequiredPdfVersion())).addNode("parameters").addConstraintViolation();
        }
        return isValid;
    }

}
