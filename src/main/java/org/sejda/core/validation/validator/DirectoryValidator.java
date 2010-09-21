/*
 * Created on 26/giu/2010
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

import org.sejda.core.validation.constraint.Directory;

/**
 * Validator for the {@link Directory} constraint.<br>
 * Note that this constraint validates that the input file exists and is a directory according to the isDirectory method of the File class.
 * 
 * @see File#isDirectory()
 * 
 * @author Andrea Vacondio
 * 
 */
public class DirectoryValidator implements ConstraintValidator<Directory, File> {

    public void initialize(Directory constraintAnnotation) {
        // on purpose
    }

    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isDirectory();
        }
        return true;
    }

}
