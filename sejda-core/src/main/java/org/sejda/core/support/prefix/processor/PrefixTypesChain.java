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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.model.exception.SejdaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds information about the {@link PrefixType}s contained in the input prefix string. Expose methods to apply the processors {@link PrefixType} chain to an input prefix and
 * request.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PrefixTypesChain {

    private static final Logger LOG = LoggerFactory.getLogger(PrefixTypesChain.class);

    // prefix types ensuring unique output names
    private Set<PrefixType> firstLevelPrefixChain = new HashSet<PrefixType>();
    // prefix types processed only if the first level processors performed some changed (ensuring unique name)
    private Set<PrefixType> secondLevelPrefixChain = new HashSet<PrefixType>();
    // processor used in case the processors chain did not perform any change
    private PrefixProcessor fallBackProcessor = new LoggingPrefixProcessorDecorator(new PrependPrefixProcessor());
    private PrefixProcessor extensionProcessor = new LoggingPrefixProcessorDecorator(
            new AppendExtensionPrefixProcessor());

    public PrefixTypesChain(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            for (PrefixType type : PrefixType.values()) {
                Matcher m = Pattern.compile(type.getMatchingRegexp()).matcher(prefix);
                if (m.find()) {
                    if (type.isEnsureUniqueNames()) {
                        firstLevelPrefixChain.add(type);
                    } else {
                        secondLevelPrefixChain.add(type);
                    }
                }
            }
        }
    }

    /**
     * the chain process the input prefix and the request
     * 
     * @param prefix
     *            input prefix to be processed
     * @param request
     *            process request
     * @return the processed string
     */
    public String process(String prefix, NameGenerationRequest request) {
        LOG.trace("Performing prefix processing with first level prefix chain");
        String retVal = processChain(prefix, request, firstLevelPrefixChain);
        // if the first level performed some change
        if (!prefix.equals(retVal)) {
            LOG.trace("Performing prefix processing with second level prefix chain");
            retVal = processChain(retVal, request, secondLevelPrefixChain);
        } else {
            retVal = fallBackProcessor.process(retVal, request);
        }
        return extensionProcessor.process(retVal, request);
    }

    private String processChain(String prefix, NameGenerationRequest request, Set<PrefixType> chain) {
        String retVal = prefix;
        for (PrefixType type : chain) {
            PrefixProcessor processor;
            try {
                processor = new LoggingPrefixProcessorDecorator(type.getProcessor().newInstance());
            } catch (InstantiationException e) {
                throw new SejdaRuntimeException(
                        String.format("Unable to instantiate processor %s", type.getProcessor()), e);
            } catch (IllegalAccessException e) {
                throw new SejdaRuntimeException(
                        String.format("Unable to instantiate processor %s", type.getProcessor()), e);
            }
            retVal = processor.process(retVal, request);
        }
        return retVal;
    }
}
