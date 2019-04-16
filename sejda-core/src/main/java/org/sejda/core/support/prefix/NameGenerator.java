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
package org.sejda.core.support.prefix;

import static java.util.Optional.ofNullable;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.core.support.prefix.processor.PrefixUtils.toSafeFilename;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.processor.PrefixTypesChain;

/**
 * Component used to generate the output name for a manipulation given the input prefix (if any);
 * 
 * @author Andrea Vacondio
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
        return toSafeFilename(prefixTypesChain.process(prefix, ofNullable(request).orElseGet(() -> nameRequest())));
    }

}
