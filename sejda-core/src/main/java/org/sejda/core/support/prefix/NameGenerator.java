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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.Sejda;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.processor.PrefixTypesChain;

/**
 * Component used to generate the output name for a manipulation given the original name and the input prefix (if any);
 * 
 * @author Andrea Vacondio
 * @see org.sejda.core.support.prefix.processor.PrefixType
 */
public final class NameGenerator {

    private String prefix;
    private String originalName;
    private PrefixTypesChain prefixTypesChain;

    private NameGenerator(String prefix, String originalName) {
        this.prefix = prefix;
        this.prefixTypesChain = new PrefixTypesChain(prefix);
        originalName(originalName);

    }

    /**
     * @param prefix
     * @param originalName
     * @return a new instance of a NameGenrator
     */
    public static NameGenerator nameGenerator(String prefix, String originalName) {
        return new NameGenerator(prefix, originalName);
    }

    /**
     * Sets the original name
     * 
     * @param originalName
     */
    private void originalName(String originalName) {
        if (StringUtils.isNotBlank(originalName)) {
            // check if the filename contains '.' and it's at least in second position (Ex. a.pdf)
            if (originalName.lastIndexOf('.') > 1) {
                this.originalName = originalName.substring(0, originalName.lastIndexOf('.'));
            } else {
                this.originalName = originalName;
            }
        }
    }

    /**
     * @param request
     *            parameters used to generate the name. It can be null.
     * @return generates a new name from the given request
     */
    public String generate(NameGenerationRequest request) {
        String generatedName = prefixTypesChain.process(prefix, preProcessRequest(request));
        return ensurePdfExtension(generatedName);
    }

    /**
     * pre process the request ensuring a not null request is returned
     * 
     * @param request
     * @return a not null request with the originalName parameter populated
     */
    private NameGenerationRequest preProcessRequest(NameGenerationRequest request) {
        NameGenerationRequest retVal = request;
        if (request == null) {
            retVal = nameRequest();
        }
        return retVal.originalName(originalName);
    }

    private String ensurePdfExtension(String name) {
        if (!name.endsWith(Sejda.PDF_EXTENSION)) {
            return String.format("%s.%s", name, Sejda.PDF_EXTENSION);
        }
        return name;
    }
}
