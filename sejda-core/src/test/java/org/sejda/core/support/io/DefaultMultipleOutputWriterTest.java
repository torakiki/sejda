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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.core.support.io.model.FileOutput;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 */
public class DefaultMultipleOutputWriterTest {

    private TaskExecutionContext context;

    @BeforeEach
    public void setUp() {
        context = new TaskExecutionContext(mock(Task.class), true);
    }

    @Test
    public void failOnFile() {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        assertThrows(TaskOutputVisitException.class, () -> new FileTaskOutput(mock(File.class)).accept(victim));
    }

    @Test
    public void moveToDir(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("out.pdf"));
        new DirectoryTaskOutput(folder.toFile()).accept(victim);
        assertEquals(1, folder.toFile().list().length);
    }

    @Test
    public void moveToFileOrDir(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("out.pdf"));
        new FileOrDirectoryTaskOutput(folder.toFile()).accept(victim);
        assertEquals(1, folder.toFile().list().length);
    }

    @Test
    public void moveToDirExisting(@TempDir Path folder) throws IOException {
        var outFile = Files.createTempFile(folder, null, null).toFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.FAIL, context);
        victim.addOutput(FileOutput.file(outFile).name(outFile.getName()));
        assertThrows(TaskOutputVisitException.class,
                () -> new DirectoryTaskOutput(outFile.getParentFile()).accept(victim));
    }

    @Test
    public void moveToDirExistingOverwrite(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        assertEquals(0, existing.length());
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.addOutput(FileOutput.file(outFile.toFile()).name(existing.getName()));
        new DirectoryTaskOutput(outDir.toFile()).accept(victim);
        assertEquals(1, Files.list(outDir).count());
        assertEquals(4, Files.list(outDir).findFirst().map(Path::toFile).map(File::length).orElse(0L));
    }

    @Test
    public void moveToDirExsistingSkip(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        assertEquals(0, existing.length());
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.SKIP, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.addOutput(FileOutput.file(outFile.toFile()).name(existing.getName()));
        new DirectoryTaskOutput(outDir.toFile()).accept(victim);
        assertEquals(1, Files.list(outDir).count());
        assertEquals(0, Files.list(outDir).findFirst().map(Path::toFile).map(File::length).orElse(0L));
    }

    @Test
    public void moveToFileDirExisting(@TempDir Path folder) throws IOException {
        var out = Files.createTempFile(folder, null, null).toFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.FAIL, context);
        victim.addOutput(FileOutput.file(out).name(out.getName()));
        assertThrows(TaskOutputVisitException.class,
                () -> new FileOrDirectoryTaskOutput(out.getParentFile()).accept(victim));
    }

    @Test
    public void moveToDirExistingRenamed(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.RENAME, context);
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name(existing.getName()));
        new DirectoryTaskOutput(outDir.toFile()).accept(victim);
        assertEquals(2, Files.list(outDir).count());
    }

    @Test
    public void addFilesRenamesExisting(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.FAIL, context);
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("myName.pdf"));
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("myName.pdf"));
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("myName.pdf"));
        var outDir = Files.createTempDirectory(folder, "sejda");
        new DirectoryTaskOutput(outDir.toFile()).accept(victim);
        assertEquals(3, Files.list(outDir).count());
    }

    @Test
    public void moveToFileOrDirExistingRenamed(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.RENAME, context);
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name(existing.getName()));
        new FileOrDirectoryTaskOutput(outDir.toFile()).accept(victim);
        assertEquals(2, Files.list(outDir).count());
    }

    @Test
    public void moveToFileOrDirInvalidOut(@TempDir Path folder) throws IOException {
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.RENAME, context);
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("a"));
        victim.addOutput(FileOutput.file(Files.createTempFile(folder, null, null).toFile()).name("b"));
        var e = assertThrows(IOException.class, () -> victim.dispatch(
                new FileOrDirectoryTaskOutput(Files.createTempFile(folder, null, null).toFile())));
        assertThat(e.getMessage(), containsString("Wrong output destination"));
    }

    @Test
    public void moveToFileOrDirSingleFile(@TempDir Path folder) throws TaskOutputVisitException, IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        assertEquals(0, existing.length());
        DefaultMultipleOutputWriter victim = new DefaultMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.addOutput(FileOutput.file(outFile.toFile()).name("out.pdf"));
        new FileOrDirectoryTaskOutput(existing).accept(victim);
        assertEquals(1, Files.list(outDir).count());
        assertEquals(4, existing.length());
    }
}
