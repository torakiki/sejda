/*
 * Created on 29/dic/2012
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
package org.sejda.model.validation.validator;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.headerfooter.Numbering;
import org.sejda.model.validation.constraint.HasHeaderFooterDefinition;

/**
 * Validates that the input parameters object has a header/footer definition.
 * 
 * @author Andrea Vacondio
 * 
 */
public class HasHeaderFooterValidator implements
        ConstraintValidator<HasHeaderFooterDefinition, SetHeaderFooterParameters> {

    public void initialize(HasHeaderFooterDefinition constraintAnnotation) {
        // nothing to do
    }

    public boolean isValid(SetHeaderFooterParameters value, ConstraintValidatorContext context) {
        if (value != null) {
            return isNotBlank(value.getLabelPrefix())
                    || (value.getNumbering() != null && !Numbering.NULL.equals(value.getNumbering()));
        }
        return true;
    }
}
