/*
 * Created on 29/ott/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.task;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.SimpleSplitParameters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 *
 */
public class BaseTaskTest {

    private TestTask victim = new TestTask();

    @Test
    public void nonNullTaskExecutionContext() throws TaskException {
        victim.before(mock(SimpleSplitParameters.class), mock(TaskExecutionContext.class));
        assertNotNull(victim.executionContext());
    }

    private static class TestTask extends BaseTask<SimpleSplitParameters> {

        @Override
        public void after() {
            // nothing

        }

        @Override
        public void before(SimpleSplitParameters parameters, TaskExecutionContext context) throws TaskException {
            super.before(parameters, context);
            // nothing
        }

        @Override
        public void execute(SimpleSplitParameters parameters) {
            // nothing
        }

    }
}
