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

import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.task.TaskExecutionContext;

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
     * @param policy
     *            policy to use in case of an existing output is found
     * @param executionContext
     *            the current execution context
     * @return a new instance of the default {@link SingleOutputWriter}
     */
    public static SingleOutputWriter newSingleOutputWriter(ExistingOutputPolicy policy,
            TaskExecutionContext executionContext) {
        return new DefaultSingleOutputWriter(policy, executionContext);
    }

    /**
     * Factory method for a {@link MultipleOutputWriter}.
     * 
     * @param policy
     *            policy to use in case of an existing output is found
     * @param executionContext
     *            the current execution context
     * @return a new instance of the default {@link MultipleOutputWriter}
     */
    public static MultipleOutputWriter newMultipleOutputWriter(ExistingOutputPolicy policy,
            TaskExecutionContext executionContext) {
        return new DefaultMultipleOutputWriter(policy, executionContext);
    }
}
