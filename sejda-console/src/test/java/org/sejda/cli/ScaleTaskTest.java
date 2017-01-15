/*
 * Created on 01 dic 2016
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.ScaleParameters;
import org.sejda.model.scale.ScaleType;

/**
 * @author Andrea Vacondio
 *
 */
public class ScaleTaskTest extends AbstractTaskTest {

    public ScaleTaskTest() {
        super(StandardTestableTask.SCALE);
    }

    @Test
    public void testScale() {
        ScaleParameters parameters = defaultCommandLine().with("-s", "0.4").invokeSejdaConsole();
        assertEquals(0.4, parameters.scale, 0);
    }

    @Test
    public void testScaleType() {
        ScaleParameters parameters = defaultCommandLine().with("-t", "page").with("-s", "1.4").invokeSejdaConsole();
        assertEquals(ScaleType.PAGE, parameters.getScaleType());
    }

    @Test
    public void testDefaults() {
        ScaleParameters parameters = defaultCommandLine().with("-s", "1").invokeSejdaConsole();
        assertEquals(ScaleType.CONTENT, parameters.getScaleType());
    }

    @Test
    public void mandatoryPageDefinitionParams() {
        defaultCommandLine().without("-s")
                .assertConsoleOutputContains("Option is mandatory");
    }
}
