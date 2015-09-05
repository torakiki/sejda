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

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * Simple prefix processor that append the extension to the input prefix. name.
 * 
 * @author Andrea Vacondio
 * 
 */
class AppendExtensionPrefixProcessor implements PrefixProcessor {

    @Override
    public String process(String inputPrefix, NameGenerationRequest request) {
        if (request != null && StringUtils.isNotBlank(request.getExtension())) {
            return String.format("%s.%s", inputPrefix, request.getExtension());
        }
        return inputPrefix;
    }

}
