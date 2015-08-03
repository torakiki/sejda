/*
 * Created on 03/08/2015
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.core.service.SplitByOutlineLevelTaskTest;
import org.sejda.model.parameter.SplitByOutlineLevelParameters;
import org.sejda.model.task.Task;

public class SplitByOutlineLevelSamboxTaskTest extends SplitByOutlineLevelTaskTest {

    @Override
    public Task<SplitByOutlineLevelParameters> getTask() {
        return new SplitByOutlineLevelTask();
    }
}
