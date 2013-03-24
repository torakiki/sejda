/*
 * Created on 24/giu/2010
 *
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
package org.sejda.core.validation;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sejda.core.context.DefaultSejdaContext;

/**
 * Default implementation of {@link ValidationContext}
 * 
 * @author Andrea Vacondio
 * 
 */
public final class DefaultValidationContext implements ValidationContext {

    private Validator validator;

    private DefaultValidationContext() {
        Configuration<?> validationConfig = Validation.byDefaultProvider().configure();
        if (new DefaultSejdaContext().isIgnoreXmlConfiguration()) {
            validationConfig.ignoreXmlConfiguration();
        }
        ValidatorFactory factory = validationConfig.buildValidatorFactory();
        validator = factory.getValidator();
    }

    public static ValidationContext getContext() {
        return DefaultValidationContextHolder.VALIDATION_CONTEXT;
    }

    public Validator getValidator() {
        return validator;
    }

    /**
     * Lazy initialization holder class
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class DefaultValidationContextHolder {

        private DefaultValidationContextHolder() {
            // hide constructor
        }

        static final DefaultValidationContext VALIDATION_CONTEXT = new DefaultValidationContext();
    }
}
