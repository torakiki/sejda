/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.core.support.prefix;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;
import org.sejda.core.support.prefix.processor.PrefixProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.ServiceLoader;

import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * Component used to generate the output name for a manipulation given the input prefix (if any);
 *
 * @author Andrea Vacondio
 */
public final class NameGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(NameGenerator.class);
    private final String prefix;

    private NameGenerator(String prefix) {
        this.prefix = StringUtils.defaultString(prefix);
    }

    /**
     * @param prefix
     * @return a new instance of a NameGenerator
     */
    public static NameGenerator nameGenerator(String prefix) {
        return new NameGenerator(prefix);
    }

    /**
     * @param request parameters used to generate the name. It cannot be null.
     * @return generates a new name from the given request
     */
    public String generate(NameGenerationRequest request) {
        requireNotNullArg(request, "Unable to generate a name for a null request");
        var context = new PrefixTransformationContext(prefix, request);
        LOG.trace("Starting processing prefix: '{}'", context.currentPrefix());
        ServiceLoader.load(PrefixProcessor.class).stream().map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(PrefixProcessor::order)).forEachOrdered(prefixProcessor -> {
                    prefixProcessor.accept(context);
                    LOG.trace("Prefix processed with {}, new value: '{}'", prefixProcessor.getClass().getSimpleName(),
                            context.currentPrefix());

                });
        return context.currentPrefix();
    }
}
