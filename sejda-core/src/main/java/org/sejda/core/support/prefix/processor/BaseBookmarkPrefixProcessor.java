/*
 * Created on 07/ott/2011
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

import java.util.regex.Matcher;

/**
 * Base class for a prefix processor replacing a prefix name with a bookmark value. A regexp can be specified to tell the processor which characters have to be removed from the
 * bookmark value (typically those non valid in a file name).
 * 
 * @author Andrea Vacondio
 * 
 */
class BaseBookmarkPrefixProcessor implements PrefixProcessor {

    private String prefixNameRegex;
    private String toBeReplacedRegex;

    BaseBookmarkPrefixProcessor(String prefixNameRegex, String toBeReplacedRegex) {
        this.prefixNameRegex = prefixNameRegex;
        this.toBeReplacedRegex = toBeReplacedRegex;
    }

    @Override
    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        if (request != null && StringUtils.isNotBlank(request.getBookmark())) {
            String bookmarkName = request.getBookmark().replaceAll(toBeReplacedRegex, "");
            if (StringUtils.isNotBlank(bookmarkName)) {
                return retVal.replaceAll(prefixNameRegex, Matcher.quoteReplacement(bookmarkName));
            }
        }
        return retVal;
    }

}