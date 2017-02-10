/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
 *
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
package org.sejda.impl.sambox;

import org.sejda.core.service.ResizePagesTaskTest;
import org.sejda.model.parameter.ResizePagesParameters;
import org.sejda.model.task.Task;

/**
 * @author Eduard Weissmann
 *
 */
public class ResizePagesSamboxTaskTest extends ResizePagesTaskTest {

    @Override
    public Task<ResizePagesParameters> getTask() {
        return new ResizePagesTask();
    }

}
