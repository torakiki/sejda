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

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator for a prefix processor performing a trace logging before and after the process.
 * 
 * @author Andrea Vacondio
 * 
 */
class LoggingPrefixProcessorDecorator implements PrefixProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingPrefixProcessorDecorator.class);

    private PrefixProcessor decorated;

    public LoggingPrefixProcessorDecorator(PrefixProcessor decorated) {
        if (decorated == null) {
            throw new IllegalArgumentException("Decorated processor cannot be null.");
        }
        this.decorated = decorated;
    }

    public String process(String inputPrefix, NameGenerationRequest request) {
        LOG.trace("Processing prefix '{}' with processor '{}'", inputPrefix, decorated.getClass());
        String retVal = decorated.process(inputPrefix, request);
        LOG.trace("Processed prefix value: '{}'", retVal);
        return retVal;
    }

}
