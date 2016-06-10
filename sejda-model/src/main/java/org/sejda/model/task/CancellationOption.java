/*
 * Created on 2 dec 2015
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.task;

import static java.util.Objects.nonNull;

public class CancellationOption {

    private TaskExecutionContext executionContext;

    public void setExecutionContext(TaskExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public boolean isCancellable() {
        return nonNull(executionContext);
    }

    public void requestCancel() {
        if (!isCancellable())
            throw new RuntimeException("Task not yet started");

        executionContext.cancelTask();
    }
}
