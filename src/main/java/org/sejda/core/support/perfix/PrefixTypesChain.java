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
package org.sejda.core.support.perfix;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.support.perfix.processor.PrefixProcessor;
import org.sejda.core.support.perfix.processor.PrependPrefixProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds information about the {@link PrefixType}s contained in the input prefix string. Expose methods to apply the processors {@link PrefixType} chain to an input prefix and request.  
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
    private PrefixProcessor fallBackProcessor = new PrependPrefixProcessor();

    public PrefixTypesChain(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            for (PrefixType type : PrefixType.values()) {
                if (prefix.matches(type.getMatchingRegexp())) {
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
        LOG.debug("Performing prefix processing with first level prefix chain");
        String retVal = processChain(prefix, request, firstLevelPrefixChain);
        // if the first level performed some change
        if (!prefix.equals(retVal)) {
            LOG.debug("Performing prefix processing with second level prefix chain");
            retVal = processChain(prefix, request, secondLevelPrefixChain);
        } else {
            retVal = fallBackProcessor.process(prefix, request);

        }
        return retVal;
    }

    private String processChain(String prefix, NameGenerationRequest request, Set<PrefixType> chain) {
        String retVal = prefix;
        for (PrefixType type : chain) {
            PrefixProcessor processor;
            try {
                processor = type.getProcessor().newInstance();
            } catch (InstantiationException e) {
                throw new SejdaRuntimeException(String
                        .format("Unable to instantiate processor %s", type.getProcessor()), e);
            } catch (IllegalAccessException e) {
                throw new SejdaRuntimeException(String
                        .format("Unable to instantiate processor %s", type.getProcessor()), e);
            }
            retVal = processor.process(retVal, request);
        }
        return retVal;
    }
}
