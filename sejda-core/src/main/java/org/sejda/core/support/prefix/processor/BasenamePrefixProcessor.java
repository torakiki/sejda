/*
 * Created on 01/lug/2010
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
package org.sejda.core.support.prefix.processor;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

/**
 * A {@link PrefixProcessor} that updates the context current prefix replacing all the [BASENAME] occurrences with the input original file name if any.
 *
 * @author Andrea Vacondio
 */
public class BasenamePrefixProcessor implements PrefixProcessor {
    private final Pattern pattern = Pattern.compile("\\[BASENAME]");

    @Override
    public void accept(PrefixTransformationContext context) {
        if (pattern.matcher(context.currentPrefix()).find()) {
            ofNullable(context.request()).map(NameGenerationRequest::getOriginalName).filter(StringUtils::isNotBlank)
                    .map(o -> context.currentPrefix().replace("[BASENAME]", o)).ifPresent(context::currentPrefix);
        }
    }

}
