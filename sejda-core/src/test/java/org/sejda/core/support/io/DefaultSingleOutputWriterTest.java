/*
 * Created on 27 apr 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.exception.TaskIOException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 */
public class DefaultSingleOutputWriterTest {
    private TaskExecutionContext context;

    @BeforeEach
    public void setUp() {
        context = new TaskExecutionContext(mock(Task.class), true);
    }

    @Test
    public void failOnDir(@TempDir Path folder) {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        assertThrows(TaskOutputVisitException.class,
                () -> new DirectoryTaskOutput(Files.createTempDirectory(folder, "sejda").toFile()).accept(victim));
    }

    @Test
    public void failOnFileOrDir(@TempDir Path folder) {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        assertThrows(TaskOutputVisitException.class,
                () -> new FileOrDirectoryTaskOutput(Files.createTempDirectory(folder, "sejda").toFile()).accept(
                        victim));
    }

    @Test
    public void missingOutput(@TempDir Path folder) {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        assertThrows(IOException.class,
                () -> victim.dispatch(new FileTaskOutput(Files.createTempFile(folder, null, null).toFile())));
    }

    @Test
    public void nonExisting(@TempDir Path folder) throws IOException, TaskIOException {
        File out = folder.resolve("not-existing.tmp").toFile();
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.taskOutput(outFile.toFile());
        victim.dispatch(new FileTaskOutput(out));
        assertEquals(4, out.length());
    }

    @Test
    public void overwrites(@TempDir Path folder) throws IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        assertEquals(0, existing.length());
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.taskOutput(outFile.toFile());
        victim.dispatch(new FileTaskOutput(existing));
        assertEquals(1, Files.list(outDir).count());
        assertEquals(4, existing.length());
    }

    @Test
    public void rename(@TempDir Path folder) throws IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.RENAME, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.taskOutput(outFile.toFile());
        victim.dispatch(new FileTaskOutput(existing));
        assertEquals(2, Files.list(outDir).count());
    }

    @Test
    public void failOnExisting(@TempDir Path folder) throws IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.FAIL, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.taskOutput(outFile.toFile());
        assertThrows(IOException.class, () -> victim.dispatch(new FileTaskOutput(existing)));
    }

    @Test
    public void skipBehavesLikeFails(@TempDir Path folder) throws IOException {
        var outDir = Files.createTempDirectory(folder, "sejda");
        var existing = Files.createTempFile(outDir, null, null).toFile();
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.SKIP, context);
        var outFile = Files.createTempFile(folder, "sejda", ".tmp");
        Files.write(outFile, new byte[] { 0, 1, 1, 1 });
        victim.taskOutput(outFile.toFile());
        assertThrows(IOException.class, () -> victim.dispatch(new FileTaskOutput(existing)));
    }
}
