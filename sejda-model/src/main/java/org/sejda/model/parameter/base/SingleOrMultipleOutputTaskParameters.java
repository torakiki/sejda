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
package org.sejda.model.parameter.base;

import org.sejda.model.output.SingleOrMultipleTaskOutput;

/**
 * Task output that accepts both a file or a folder.
 * The correctness of the output type chosen will be validated at runtime, when results are written out.
 */
public interface SingleOrMultipleOutputTaskParameters extends PrefixableTaskParameters {

    /**
     * Sets the output destination
     * 
     * @param output
     */
    void setOutput(SingleOrMultipleTaskOutput output);
}
