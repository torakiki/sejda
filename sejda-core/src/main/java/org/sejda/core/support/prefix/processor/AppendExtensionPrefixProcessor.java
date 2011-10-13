/*
 * Created on 24/ago/2011
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
 * Simple prefix processor that append the extension to the input prefix. name.
 * 
 * @author Andrea Vacondio
 * 
 */
class AppendExtensionPrefixProcessor implements PrefixProcessor {

    public String process(String inputPrefix, NameGenerationRequest request) {
        if (request != null && StringUtils.isNotBlank(request.getExtension())) {
            return String.format("%s.%s", inputPrefix, request.getExtension());
        }
        return inputPrefix;
    }

}
