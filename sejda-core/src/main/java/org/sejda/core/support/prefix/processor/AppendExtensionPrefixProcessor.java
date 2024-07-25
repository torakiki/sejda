/*
 * Created on 24/ago/2011
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

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static java.util.Optional.ofNullable;

/**
 * A {@link PrefixProcessor} that updates the context current prefix adding the extension of the request.
 *
 * @author Andrea Vacondio
 */
public class AppendExtensionPrefixProcessor implements PrefixProcessor {

    @Override
    public void accept(PrefixTransformationContext context) {
        ofNullable(context.request()).map(NameGenerationRequest::getExtension)
                .map(e -> String.format("%s.%s", context.currentPrefix(), e)).ifPresent(context::currentPrefix);
    }

    @Override
    public int order() {
        return 1000;
    }
}
