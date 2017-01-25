/*
 * Created on 22 ott 2016
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
import org.sejda.model.parameter.WatermarkParameters;
import org.sejda.model.watermark.Location;

/**
 * @author Andrea Vacondio
 *
 */
public class WatermarkTaskTest extends AbstractTaskTest {

    public WatermarkTaskTest() {
        super(StandardTestableTask.WATERMARK);
    }

    @Test
    public void testAlpha() {
        WatermarkParameters parameters = defaultCommandLine().with("-a", "80").invokeSejdaConsole();
        assertEquals(80, parameters.getOpacity());
    }

    @Test
    public void testLocation() {
        WatermarkParameters parameters = defaultCommandLine().with("-l", "over").invokeSejdaConsole();
        assertEquals(Location.OVER, parameters.getLocation());
    }

    @Test
    public void testPosition() {
        WatermarkParameters parameters = defaultCommandLine().with("-c", "80,400").invokeSejdaConsole();
        assertEquals(80, (int) parameters.getPosition().getX());
        assertEquals(400, (int) parameters.getPosition().getY());
    }

    @Test
    public void testDimension() {
        WatermarkParameters parameters = defaultCommandLine().with("-d", "200x55").invokeSejdaConsole();
        assertEquals(200, parameters.getDimension().getWidth(), 0);
        assertEquals(55, parameters.getDimension().getHeight(), 0);
    }

    @Test
    public void testDefaults() {
        WatermarkParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(Location.BEHIND, parameters.getLocation());
        assertEquals(100, parameters.getOpacity());
    }
}
