/*
 * Created on 01/lug/2010
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
 * Process the input prefix replacing all the [CURRENTPAGE] or [CURRENTPAGE##] occurrences with the input current page number (formatted with the given pattern identified by the
 * number of # and incremented by the starting number if found). Ex:
 * <p>
 * <b>[CURRENTPAGE]_BLA_[CURRENTPAGE####]_LAB</b> and given page number <b>2</b> will produce <b>2_BLA_0002_LAB</b>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
class CurrentPagePrefixProcessor extends NumberPrefixProcessor {

    CurrentPagePrefixProcessor() {
        super("CURRENTPAGE");
    }

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = "";
        if (request != null && request.getPage() != null) {
            retVal = findAndReplace(inputPrefix, request.getPage());
        }
        return (StringUtils.isBlank(retVal)) ? inputPrefix : retVal;
    }

}
