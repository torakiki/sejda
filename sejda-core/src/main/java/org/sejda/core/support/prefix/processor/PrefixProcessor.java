/*
 * Created on 29/giu/2010
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
package org.sejda.core.support.prefix.processor;

import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * A Processor takes a prefix string and applies the transformation it's designed for. Please consider that we currently reuse instances across multiple parameters execution
 * therefor implementations have to be stateless to avoid unexpected behavior.
 * 
 * @author Andrea Vacondio
 */
@FunctionalInterface
interface PrefixProcessor {

    /**
     * Process the input prefix String based on the input request returning the processed String
     * 
     * @param inputPrefix
     *            input prefix String
     * @param request
     *            name generation request. It can be null, the processor should handle it without throwing a NullPointerException.
     * @return the post processed inputPrefix
     */
    String process(String inputPrefix, NameGenerationRequest request);
}
