/*
 * Created on 03 mag 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.support.prefix.processor;

import static java.util.Objects.nonNull;

import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * Simple prefix processor that prepend the page number to the given input
 * 
 * @author Andrea Vacondio
 *
 */
public class PrependPageNumberPrefixProcessor implements PrefixProcessor {

    @Override
    public String process(String inputPrefix, NameGenerationRequest request) {
        if (nonNull(request) && nonNull(request.getPage())) {
            return String.format("%d_%s", request.getPage(), inputPrefix);
        }
        return inputPrefix;
    }

}
