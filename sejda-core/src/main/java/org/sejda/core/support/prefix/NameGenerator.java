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
package org.sejda.core.support.prefix;

import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.processor.PrefixTypesChain;

/**
 * Component used to generate the output name for a manipulation given the input prefix (if any);
 * 
 * @author Andrea Vacondio
 * @see org.sejda.core.support.prefix.processor.PrefixType
 */
public final class NameGenerator {

    private String prefix;
    private PrefixTypesChain prefixTypesChain;

    private NameGenerator(String prefix) {
        this.prefix = StringUtils.defaultString(prefix);
        this.prefixTypesChain = new PrefixTypesChain(prefix);
    }

    /**
     * @param prefix
     * @return a new instance of a NameGenerator
     */
    public static NameGenerator nameGenerator(String prefix) {
        return new NameGenerator(prefix);
    }

    /**
     * @param request
     *            parameters used to generate the name. It cannot be null.
     * @return generates a new name from the given request
     */
    public String generate(NameGenerationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Unable to generate a name for a null request.");
        }
        return prefixTypesChain.process(prefix, preProcessRequest(request));
    }

    /**
     * pre process the request ensuring a not null request is returned
     * 
     * @param request
     * @return a not null request.
     */
    private NameGenerationRequest preProcessRequest(NameGenerationRequest request) {
        NameGenerationRequest retVal = request;
        if (request == null) {
            retVal = nameRequest();
        }
        return retVal;
    }
}
