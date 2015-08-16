/*
 * Created on 18/ott/2011
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
package org.sejda.core.support.io;

/**
 * Provides factory methods for available output writers.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class OutputWriters {

    private OutputWriters() {
        // hide
    }

    /**
     * Factory method for a {@link SingleOutputWriter}.
     * 
     * @param overwrite
     *            true if the writer should overwrite existing output
     * @return a new instace of the default {@link SingleOutputWriter}
     */
    public static SingleOutputWriter newSingleOutputWriter(boolean overwrite) {
        return new DefaultSingleOutputWriter(overwrite);
    }

    /**
     * Factory method for a {@link MultipleOutputWriter}.
     * 
     * @param overwrite
     *            true if the writer should overwrite existing output
     * @return a new instace of the default {@link MultipleOutputWriter}
     */
    public static MultipleOutputWriter newMultipleOutputWriter(boolean overwrite) {
        return new DefaultMultipleOutputWriter(overwrite);
    }
}
