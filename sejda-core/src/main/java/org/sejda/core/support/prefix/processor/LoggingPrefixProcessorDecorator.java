/*
 * Created on 24/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

    @Override
    public String process(String inputPrefix, NameGenerationRequest request) {
        LOG.trace("Processing prefix '{}' with processor '{}'", inputPrefix, decorated.getClass());
        String retVal = decorated.process(inputPrefix, request);
        LOG.trace("Processed prefix value: '{}'", retVal);
        return retVal;
    }

}
