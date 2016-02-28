/*
 * Created on 27 feb 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.input;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.validation.constraint.NotEmpty;

/**
 * Skeletal implementation for an input source.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the generic type of the source
 */
public abstract class AbstractSource<T> implements Source<T> {
    @NotEmpty
    private final String name;

    /**
     * Creates a source with the given name.
     * 
     * @param name
     * @throws IllegalArgumentException
     *             if the name is blank
     */
    public AbstractSource(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException("A not blank name are expected.");
        }
        this.name = name;
    }

    /**
     * @return the name of this source
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).toString();
    }
}
