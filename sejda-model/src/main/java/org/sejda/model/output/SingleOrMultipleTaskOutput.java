/*
 * Copyright 2017 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.output;

/**
 * For tasks that support batch processing multiple files at once,
 * but can also take a single file as input and generate a single output file
 *
 * Eg: rotate can work on multiple files, so then a directory output is expected.
 * But, when rotating a single file, a file output can be provided.
 * 
 */
public interface SingleOrMultipleTaskOutput extends TaskOutput {
    // define further methods
}
