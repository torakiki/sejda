/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.support.prefix.processor;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * Simple prefix processor that prepend the input prefix to the original name. If the request contains a page number, the number is prepended to the prefix and to the original
 * name.
 * 
 * @author Andrea Vacondio
 * 
 */
class PrependPrefixProcessor implements PrefixProcessor {

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
