/*
 * Created on 29/giu/2010
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
package org.sejda.core.support.perfix.processor;

import org.sejda.core.support.perfix.NameGenerationRequest;

/**
 * A Processor takes a prefix string and applies the transformation it's designed for.
 * 
 * @author Andrea Vacondio
 */
public interface PrefixProcessor {

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
