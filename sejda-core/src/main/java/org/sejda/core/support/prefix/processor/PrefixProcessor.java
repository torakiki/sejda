/*
 * Created on 29/giu/2010
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

import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import java.util.function.Consumer;

/**
 * A Processor takes a {@link PrefixTransformationContext} and, if necessary, updates the context applying the transformation it's designed for.
 *
 * @author Andrea Vacondio
 */
public interface PrefixProcessor extends Consumer<PrefixTransformationContext> {

    /**
     * @return an int representing the order in which the processor should be applied. The lower the number the earlier the processor will be applied.
     */
    default int order() {
        return 0;
    }
}
