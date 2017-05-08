/*
 * Created on 19/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.core.support.io.model.FileOutput;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

/**
 * @author Andrea Vacondio
 * 
 */
public class DefaultMultipleOutputWriterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private TaskExecutionContext context;

    @Before
    public void setUp() {
        context = new TaskExecutionContext(mock(Task.class), true);
    }

    @Test(expected = TaskOutputVisitException.class)
    public void failOnFile() throws TaskOutputVisitException {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        new FileTaskOutput(mock(File.class)).accept(victim);
    }

    @Test
    public void moveToDir() throws TaskOutputVisitException, IOException {
        File out = folder.newFolder();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        victim.addOutput(FileOutput.file(folder.newFile()).name("out.pdf"));
        new DirectoryTaskOutput(out).accept(victim);
        assertEquals(1, out.list().length);
    }

    @Test
    public void moveToFileOrDir() throws TaskOutputVisitException, IOException {
        File out = folder.newFolder();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        victim.addOutput(FileOutput.file(folder.newFile()).name("out.pdf"));
        new FileOrDirectoryTaskOutput(out).accept(victim);
        assertEquals(1, out.list().length);
    }

    @Test(expected = TaskOutputVisitException.class)
    public void moveToDirExsisting() throws TaskOutputVisitException, IOException {
        File outFile = folder.newFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.FAIL, context);
        victim.addOutput(FileOutput.file(outFile).name(outFile.getName()));
        new DirectoryTaskOutput(outFile.getParentFile()).accept(victim);
    }

    @Test
    public void moveToDirExsistingOverwrite() throws TaskOutputVisitException, IOException {
        File existing = folder.newFile();
        assertEquals(0, existing.length());
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        Path outFile = Files.createTempFile("sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.addOutput(FileOutput.file(outFile.toFile()).name(existing.getName()));
        new DirectoryTaskOutput(existing.getParentFile()).accept(victim);
        assertEquals(1, existing.getParentFile().list().length);
        assertEquals(4, existing.getParentFile().listFiles()[0].length());
        Files.deleteIfExists(outFile);
    }

    @Test
    public void moveToDirExsistingSkip() throws TaskOutputVisitException, IOException {
        File existing = folder.newFile();
        assertEquals(0, existing.length());
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.SKIP, context);
        Path outFile = Files.createTempFile("sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.addOutput(FileOutput.file(outFile.toFile()).name(existing.getName()));
        new DirectoryTaskOutput(existing.getParentFile()).accept(victim);
        assertEquals(1, existing.getParentFile().list().length);
        assertEquals(0, existing.getParentFile().listFiles()[0].length());
        Files.delete(outFile);
    }

    @Test(expected = TaskOutputVisitException.class)
    public void moveToFileDirExsisting() throws TaskOutputVisitException, IOException {
        File outFile = folder.newFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.FAIL, context);
        victim.addOutput(FileOutput.file(outFile).name(outFile.getName()));
        new FileOrDirectoryTaskOutput(outFile.getParentFile()).accept(victim);
    }

    @Test
    public void moveToDirExsistingRenamed() throws TaskOutputVisitException, IOException {
        File outFile = folder.newFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.RENAME, context);
        victim.addOutput(FileOutput.file(folder.newFile()).name(outFile.getName()));
        new DirectoryTaskOutput(outFile.getParentFile()).accept(victim);
        assertEquals(2, outFile.getParentFile().list().length);
    }

    @Test
    public void moveToFileOrDirExsistingRenamed() throws TaskOutputVisitException, IOException {
        File outFile = folder.newFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.RENAME, context);
        victim.addOutput(FileOutput.file(folder.newFile()).name(outFile.getName()));
        new FileOrDirectoryTaskOutput(outFile.getParentFile()).accept(victim);
        assertEquals(2, outFile.getParentFile().list().length);
    }

    @Test(expected = TaskOutputVisitException.class)
    public void moveToFileOrDirInvalidOut() throws TaskOutputVisitException, IOException {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.RENAME, context);
        victim.addOutput(FileOutput.file(folder.newFile()).name("a"));
        victim.addOutput(FileOutput.file(folder.newFile()).name("b"));
        new FileOrDirectoryTaskOutput(folder.newFile()).accept(victim);
    }

    @Test
    public void moveToFileOrDirSingleFile() throws TaskOutputVisitException, IOException {
        File out = folder.newFile();
        assertEquals(0, out.length());
        Path outFile = Files.createTempFile("sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        victim.addOutput(FileOutput.file(outFile.toFile()).name("out.pdf"));
        new FileOrDirectoryTaskOutput(out).accept(victim);
        assertEquals(4, out.length());
        Files.deleteIfExists(outFile);
    }
}
