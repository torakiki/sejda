/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.support.prefix.processor;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
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
    private Set<PrefixType> firstLevelPrefixChain = new HashSet<>();
    // prefix types processed only if the first level processors performed some changed (ensuring unique name)
    private Set<PrefixType> secondLevelPrefixChain = new HashSet<>();
    // processor used in case the processors chain did not perform any change
    private PrefixProcessor fallBackProcessor = new LoggingPrefixProcessorDecorator(new PrependPrefixProcessor());
    private PrefixProcessor extensionProcessor = new LoggingPrefixProcessorDecorator(
            new AppendExtensionPrefixProcessor());

    public PrefixTypesChain(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            for (PrefixType type : PrefixType.values()) {
                if (type.isFoundIn(prefix)) {
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
            retVal = new LoggingPrefixProcessorDecorator(type.getProcessor()).process(retVal, request);
        }
        return retVal;
    }
}
