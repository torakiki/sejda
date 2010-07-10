/*
 * Created on 09/lug/2010
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
package org.sejda.core.validation.validator;

import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.core.validation.constraint.ExistingFile;

/**
 * Validates that the given file exists
 * 
 * @author Andrea Vacondio
 * 
 */
public class ExistingFileValidator implements ConstraintValidator<ExistingFile, File> {

    public void initialize(ExistingFile constraintAnnotation) {
        // on purpose
    }

    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.exists();
        }
        return true;
    }

}
