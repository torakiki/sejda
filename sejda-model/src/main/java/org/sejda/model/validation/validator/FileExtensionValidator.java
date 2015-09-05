/*
 * Created on 24/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.validation.validator;

import static org.apache.commons.io.FilenameUtils.indexOfExtension;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.io.FilenameUtils;
import org.sejda.model.validation.constraint.FileExtension;

/**
 * Validates the extension of the a {@link File}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class FileExtensionValidator implements ConstraintValidator<FileExtension, File> {

    private String expectedExtension;

    @Override
    public void initialize(FileExtension constraintAnnotation) {
        expectedExtension = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null && value.isFile()) {
            String extension = FilenameUtils.getExtension(value.getName());
            return equalsIgnoreCase(expectedExtension, extension) && indexOfExtension(value.getName()) > 0;
        }
        return true;
    }

}
