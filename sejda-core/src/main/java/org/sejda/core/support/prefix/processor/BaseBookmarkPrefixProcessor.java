/*
 * Created on 07/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * Base class for a prefix processor replacing a prefix name with a bookmark value. A regexp can be specified to tell the processor which characters have to be removed from the
 * bookmark value (typically those non valid in a file name).
 * 
 * @author Andrea Vacondio
 * 
 */
class BaseBookmarkPrefixProcessor implements PrefixProcessor {

    private String prefixNameRegex;
    private String toBeReplacedRegex;

    BaseBookmarkPrefixProcessor(String prefixNameRegex, String toBeReplacedRegex) {
        this.prefixNameRegex = prefixNameRegex;
        this.toBeReplacedRegex = toBeReplacedRegex;
    }

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        if (request != null && StringUtils.isNotBlank(request.getBookmark())) {
            String bookmarkName = request.getBookmark().replaceAll(toBeReplacedRegex, "");
            if (StringUtils.isNotBlank(bookmarkName)) {
                return retVal.replaceAll(prefixNameRegex, bookmarkName);
            }
        }
        return retVal;
    }

}