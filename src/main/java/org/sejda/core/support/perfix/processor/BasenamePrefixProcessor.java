/*
 * Created on 01/lug/2010
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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.support.perfix.model.NameGenerationRequest;

/**
 * Process the input prefix replacing all the [BASENAME] occurrences with the input original file name if any.
 * 
 * @author Andrea Vacondio
 * 
 */
public class BasenamePrefixProcessor implements PrefixProcessor {

    private static final String BASENAME_REPLACE_REGX = "\\[BASENAME\\]";

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        if (request != null && StringUtils.isNotBlank(request.getOriginalName())) {
            return retVal.replaceAll(BASENAME_REPLACE_REGX, request.getOriginalName());
        }
        return retVal;
    }

}
