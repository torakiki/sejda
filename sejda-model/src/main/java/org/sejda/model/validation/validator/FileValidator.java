/*
 * Created on 23/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.sejda.model.validation.constraint.IsFile;

import java.io.File;

/**
 * Constraint validating that an instance of {@link File} is actually a file and not a directory.
 *
 * @author Andrea Vacondio
 */
public class FileValidator implements ConstraintValidator<IsFile, File> {

    @Override
    public boolean isValid(File value, ConstraintValidatorContext context) {
        if (value != null && value.exists()) {
            return value.isFile();
        }
        return true;
    }

}
