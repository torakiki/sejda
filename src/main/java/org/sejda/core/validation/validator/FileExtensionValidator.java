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
package org.sejda.core.validation.validator;

import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.core.validation.constraint.FileExtension;

/**
 * Validates the extension of the a file
 * 
 * @author Andrea Vacondio
 * 
 */
public class FileExtensionValidator implements ConstraintValidator<FileExtension, File> {

    private String expectedExtension;

    public void initialize(FileExtension constraintAnnotation) {
        expectedExtension = constraintAnnotation.value().toLowerCase();
    }

    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null) {
            String fileName = value.getName().toLowerCase();
            return fileName.endsWith("." + expectedExtension) && fileName.length() > (expectedExtension.length() + 1);
        }
        return true;
    }

}
