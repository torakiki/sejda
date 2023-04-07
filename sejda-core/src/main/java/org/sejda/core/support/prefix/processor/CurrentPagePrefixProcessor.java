/*
 * Created on 01/lug/2010
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
package org.sejda.core.support.prefix.processor;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static java.util.Optional.ofNullable;

/**
 * A {@link PrefixProcessor} replacing all the [CURRENTPAGE] or [CURRENTPAGE##] or [CURRENTPAGE##11] or [CURRENTPAGE11] occurrences with the input current page number (formatted with
 * the given pattern identified by the number of # and incremented by the starting number if found). Ex:
 * <p>
 * <b>[CURRENTPAGE]_BLA_[CURRENTPAGE####]_LAB</b> and given page number <b>2</b> will produce <b>2_BLA_0002_LAB</b>
 * </p>
 *
 * @author Andrea Vacondio
 */
public class CurrentPagePrefixProcessor extends NumberPrefixProcessor implements PrefixProcessor {

    public CurrentPagePrefixProcessor() {
        super("CURRENTPAGE");
    }

    @Override
    public void accept(PrefixTransformationContext context) {
        if (pattern.matcher(context.currentPrefix()).find()) {
            ofNullable(context.request()).map(NameGenerationRequest::getPage)
                    .map(p -> findAndReplace(context.currentPrefix(), p)).filter(StringUtils::isNotBlank)
                    .ifPresent(s -> {
                        context.uniqueNames(true);
                        context.currentPrefix(s);
                    });
        }
    }

}
