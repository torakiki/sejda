/*
 * Created on 30 dic 2016
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
package org.sejda.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.cli.command.TestableTask;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * @author Andrea Vacondio
 *
 */
public class LenientTraitTest extends AcrossAllTasksTraitTest {
    public LenientTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void lenient() {

        TaskParameters result = defaultCommandLine().withFlag("--lenient").invokeSejdaConsole();
        assertTrue(result.isLenient());
    }

    @Test
    public void nonLenient() {
        TaskParameters result = defaultCommandLine().without("--lenient").invokeSejdaConsole();
        assertFalse(result.isLenient());
    }
}
