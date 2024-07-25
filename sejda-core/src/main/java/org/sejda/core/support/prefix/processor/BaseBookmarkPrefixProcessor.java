/*
 * Created on 07/ott/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

/**
 * Base class for a prefix processor replacing a prefix name with a bookmark value. A regexp can be specified to tell the processor which characters have to be removed from the
 * bookmark value (typically those not valid in a file name).
 *
 * @author Andrea Vacondio
 */
class BaseBookmarkPrefixProcessor implements PrefixProcessor {

    private final Pattern pattern;
    private final String invalidCharsRegexp;

    BaseBookmarkPrefixProcessor(String prefixNameRegex, String invalidCharsRegexp) {
        this.pattern = Pattern.compile(prefixNameRegex);
        this.invalidCharsRegexp = invalidCharsRegexp;
    }

    @Override
    public void accept(PrefixTransformationContext context) {
        var matcher = pattern.matcher(context.currentPrefix());
        if (matcher.find()) {
            ofNullable(context.request()).map(NameGenerationRequest::getBookmark)
                    .map(b -> b.replaceAll(invalidCharsRegexp, "")).map(Matcher::quoteReplacement)
                    .filter(StringUtils::isNotBlank).ifPresent(r -> {
                        context.uniqueNames(true);
                        context.currentPrefix(matcher.replaceAll(r));
                    });
        }
    }

}