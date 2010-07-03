/*
 * Created on 03/lug/2010
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
import org.sejda.core.support.perfix.NameGenerationRequest;

/**
 * Simple prefix processor that prepend the input prefix to the original name. If the request contains a page number, the number is prepended to the prefix and to the original name.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PrependPrefixProcessor implements PrefixProcessor {

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        if (request != null && StringUtils.isNotBlank(request.getOriginalName())) {
            retVal += request.getOriginalName();
            if (request.getPage() != null) {
                retVal = String.format("%d_%s", request.getPage(), retVal);
            }
        }
        return retVal;
    }

}
