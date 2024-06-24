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

import org.apache.commons.lang3.function.FailableConsumer;

/**
 * A Rule to be applied during the PDF/A conversion
 *
 * @author Andrea Vacondio
 */
public interface Rule<T, E extends Exception> extends FailableConsumer<T, E> {
    /**
     * @return the context for this conversion
     */
    ConversionContext conversionContext();
}
