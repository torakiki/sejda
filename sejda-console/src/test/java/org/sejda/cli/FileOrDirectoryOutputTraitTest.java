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

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.base.TaskParameters;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * For tasks that support both single file or directory as output, depending on how many inputs were given
 * 
 * @author Eduard Weissmann
 * 
 */
public class FileOrDirectoryOutputTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(TestableTasks.getTasksWith(t -> TestableTasks.hasFileOrFolderOutput(t)));
    }

    public FileOrDirectoryOutputTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void fileAsOutput() throws TaskException {
        assertFalse(new File("./outputs/fileOutput.pdf").exists());

        TaskParameters result = defaultCommandLine().with("-o", "./outputs/fileOutput.pdf").invokeSejdaConsole();
        assertOutputFile(result.getOutput(), Paths.get("./outputs/fileOutput.pdf"));
    }

    @Test
    public void folderAsOutput() throws TaskException {
        assertTrue(new File("./outputs/").exists());

        TaskParameters result = defaultCommandLine().with("-o", "./outputs/").invokeSejdaConsole();
        assertOutputFile(result.getOutput(), Paths.get("./outputs/"));
    }

    @Test
    public void overwrite() {
        TaskParameters result = defaultCommandLine().withFlag("--overwrite").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.OVERWRITE, result.getExistingOutputPolicy());
    }

    @Test
    public void overwriteHiddenParam() {
        TaskParameters result2 = defaultCommandLine().with("--existingOutput", "overwrite").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.OVERWRITE, result2.getExistingOutputPolicy());
    }

    @Test
    public void dontOverwrite() {
        TaskParameters result = defaultCommandLine().invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.FAIL, result.getExistingOutputPolicy());

        TaskParameters result2 = defaultCommandLine().with("--existingOutput", "skip").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.SKIP, result2.getExistingOutputPolicy());

        TaskParameters result3 = defaultCommandLine().with("--existingOutput", "fail").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.FAIL, result3.getExistingOutputPolicy());

        TaskParameters result4 = defaultCommandLine().with("--existingOutput", "rename").invokeSejdaConsole();
        assertEquals(ExistingOutputPolicy.RENAME, result4.getExistingOutputPolicy());
    }
}
