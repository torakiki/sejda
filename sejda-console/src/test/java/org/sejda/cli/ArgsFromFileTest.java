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

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.sejda.model.RectangularBox;
import org.sejda.model.parameter.CropParameters;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * Tests for providing args via txt file
 * 
 * @author Eduard Weissmann
 * 
 */
public class ArgsFromFileTest extends AcrossAllTasksTraitTest {

    public ArgsFromFileTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void readArgsFromFile() throws IOException {
        File tmp = File.createTempFile("console", "args.txt");
        tmp.deleteOnExit();
        FileUtils.write(tmp, defaultCommandLine().toCommandLineString());

        new CommandLineExecuteTestHelper(true).invokeConsoleAndReturnTaskParameters(tmp.getAbsolutePath());
    }
}
