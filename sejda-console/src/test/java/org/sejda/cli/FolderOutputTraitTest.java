/*
 * Created on Aug 25, 2011
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

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * For tasks that support a folder as output, test various scenarios related to this trait
 * 
 * @author Eduard Weissmann
 * 
 */
public class FolderOutputTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(TestableTasks.getTasksWith(TestableTasks::hasFolderOutput));
    }

    public FolderOutputTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void negative_NotFound() {
        defaultCommandLine().with("-o", "output-doesntexist")
                .assertConsoleOutputContains("is not an existing directory");
    }

    @Test
    public void positive() throws TaskException {
        TaskParameters result = defaultCommandLine().with("-o", "./outputs").invokeSejdaConsole();
        assertOutputFolder(result, Paths.get("./outputs"));
    }

    @Test
    public void overwrite() {
        TaskParameters result = defaultCommandLine().with("--existingOutput", "overwrite").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.OVERWRITE, result.getExistingOutputPolicy());
    }

    @Test
    public void skip() {
        TaskParameters result = defaultCommandLine().with("--existingOutput", "skip").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.SKIP, result.getExistingOutputPolicy());
    }

    @Test
    public void rename() {
        TaskParameters result = defaultCommandLine().with("--existingOutput", "rename").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.RENAME, result.getExistingOutputPolicy());
    }

    @Test
    public void defaultPolicy() {
        TaskParameters result = defaultCommandLine().invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.FAIL, result.getExistingOutputPolicy());
    }

}
