/*
 * Created on 12/mag/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.core.service;

import org.sejda.model.exception.TaskException;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;

/**
 * @author Andrea Vacondio
 * 
 */
public class TestTask extends BaseTask<TestTaskParameter> {

    @Override
    public void after() {
        // nothing

    }

    @Override
    public void before(TestTaskParameter parameters, TaskExecutionContext context) throws TaskException {
        super.before(parameters, context);
        // nothing
    }

    @Override
    public void execute(TestTaskParameter parameters) {
        // nothing
    }

}
