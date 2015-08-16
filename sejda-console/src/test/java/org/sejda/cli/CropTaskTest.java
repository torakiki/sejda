/*
 * Created on Sep 30, 2011
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.junit.Test;
import org.sejda.model.RectangularBox;
import org.sejda.model.parameter.CropParameters;

/**
 * Tests for the Crop task
 * 
 * @author Eduard Weissmann
 * 
 */
public class CropTaskTest extends AbstractTaskTest {

    public CropTaskTest() {
        super(TestableTask.CROP);
    }

    @Test
    public void cropAreas_unrecognizedLeft() {
        defaultCommandLine().with("--cropAreas", "[1:a][3:4]").assertConsoleOutputContains("Unrecognized left: 'a'");
    }

    @Test
    public void cropAreas_unrecognizedTop() {
        defaultCommandLine().with("--cropAreas", "[1:4][c:3]").assertConsoleOutputContains("Unrecognized top: 'c'");
    }

    @Test
    public void cropAreas_unrecognizedRight() {
        defaultCommandLine().with("--cropAreas", "[1:4][3:b]").assertConsoleOutputContains("Unrecognized right: 'b'");
    }

    @Test
    public void cropAreas_unrecognizedBottom() {
        defaultCommandLine().with("--cropAreas", "[d:4][2:3]").assertConsoleOutputContains("Unrecognized bottom: 'd'");
    }

    @Test
    public void tooFewTokensInCropArea() {
        defaultCommandLine().with("--cropAreas", "[4:2][3]").assertConsoleOutputContains(
                "Unparsable rectangular box: '[4:2][3]'. Expected format is: ");
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("--cropAreas").assertConsoleOutputContains("Option is mandatory: --cropAreas");
    }

    @Test
    public void cropAreas_positive() {
        CropParameters parameters = defaultCommandLine().with("--cropAreas", "[1:2][3:4] [21:22][23:24]")
                .invokeSejdaConsole();

        assertThat(parameters.getCropAreas(),
                hasItems(RectangularBox.newInstance(1, 2, 3, 4), RectangularBox.newInstance(21, 22, 23, 24)));

        assertThat(parameters.getCropAreas().size(), is(2));
    }
}
