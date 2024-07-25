/*
 * Created on 03 mag 2017
 * Copyright 2017 Sober Lemur S.r.l. and Sejda BV.
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

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static java.util.Optional.ofNullable;

/**
 * A {@link PrefixProcessor} that prepends the page number to the current prefix if the current status doesn't guarantee unique names.
 *
 * @author Andrea Vacondio
 */
public class PrependPageNumberPrefixProcessor implements PrefixProcessor {

    @Override
    public void accept(PrefixTransformationContext context) {
        if (!context.uniqueNames()) {
            ofNullable(context.request()).map(NameGenerationRequest::getPage)
                    .map(p -> String.format("%d_%s", p, context.currentPrefix())).ifPresent(context::currentPrefix);
        }
    }

    @Override
    public int order() {
        return 200;
    }
}
