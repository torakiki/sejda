/*
 * Created on 24/giu/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.validation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sejda.core.configuration.GlobalConfiguration;

/**
 * Default implementation of {@link ValidationContext}
 * 
 * @author Andrea Vacondio
 * 
 */
public final class DefaultValidationContext implements ValidationContext {

    private Validator validator;
    private boolean validation = false;
    private static ValidationContext instance = null;

    private DefaultValidationContext() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validation = GlobalConfiguration.getInstance().isValidation();
    }

    public static synchronized ValidationContext getContext() {
        if (instance == null) {
            instance = new DefaultValidationContext();
        }
        return instance;
    }

    public Validator getValidator() {
        return validator;
    }

    public boolean isValidation() {
        return validation;
    }

}
