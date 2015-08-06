/*
 * Created on Aug 29, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * Test verifying that the --overwrite flag can be specified for each task
 * 
 * @author Eduard Weissmann
 * 
 */
public class OverwriteFlagTraitTest extends AcrossAllTasksTraitTest {

    public OverwriteFlagTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void onValue() {
        TaskParameters result = defaultCommandLine().withFlag("--overwrite").invokeSejdaConsole();
        assertTrue(describeExpectations(), result.isOverwrite());
    }

    @Test
    public void offValue() {
        TaskParameters result = defaultCommandLine().invokeSejdaConsole();

        assertFalse(describeExpectations(), result.isOverwrite());
    }
}
