package org.sejda.core.support.prefix.processor;
/*
 * Created on 06/04/23
 * Copyright 2023 Sober Lemur S.r.l. and Sejda BV
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

import org.sejda.core.support.prefix.model.PrefixTransformationContext;
import org.sejda.model.util.IOUtils;

/**
 * A {@link PrefixProcessor} that converts the current prefix to a safe filename
 *
 * @author Andrea Vacondio
 */
public class ToSafeFilenamePrefixProcessor implements PrefixProcessor {

    @Override
    public void accept(PrefixTransformationContext context) {
        context.currentPrefix(IOUtils.toSafeFilename(context.currentPrefix()));
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }
}
