/*
 * Created on 24/giu/2010
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
package org.sejda.core.validation;

import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.sejda.core.context.DefaultSejdaConfiguration;

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
        if (DefaultSejdaConfiguration.getInstance().isValidationIgnoringXmlConfiguration()) {
            validationConfig.ignoreXmlConfiguration();
        }
        ValidatorFactory factory = validationConfig.buildValidatorFactory();
        validator = factory.getValidator();
    }

    public static ValidationContext getContext() {
        return DefaultValidationContextHolder.VALIDATION_CONTEXT;
    }

    @Override
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
