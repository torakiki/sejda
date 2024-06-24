package org.sejda.impl.sambox.component.pdfa;
/*
 * Created on 21/06/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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

import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * Base class for user-defined {@link Rule} with conversion context.
 *
 * @author Andrea Vacondio
 */
abstract class BaseRule<T, E extends Exception> implements Rule<T, E> {

    private final ConversionContext conversionContext;

    public BaseRule(ConversionContext conversionContext) {
        requireNotNullArg(conversionContext, "Conversion context cannot be null");
        this.conversionContext = conversionContext;
    }

    public ConversionContext conversionContext() {
        return conversionContext;
    }
}
