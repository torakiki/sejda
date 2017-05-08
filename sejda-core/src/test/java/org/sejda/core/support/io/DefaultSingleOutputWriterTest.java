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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskOutputVisitException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

/**
 * @author Andrea Vacondio
 */
public class DefaultSingleOutputWriterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private TaskExecutionContext context;

    @Before
    public void setUp() {
        context = new TaskExecutionContext(mock(Task.class), true);
    }

    @Test(expected = TaskOutputVisitException.class)
    public void failOnDir() throws TaskOutputVisitException, IOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        new DirectoryTaskOutput(folder.newFolder()).accept(victim);
    }

    @Test(expected = TaskOutputVisitException.class)
    public void failOnFileOrDir() throws TaskOutputVisitException, IOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        new FileOrDirectoryTaskOutput(folder.newFolder()).accept(victim);
    }

    @Test(expected = IOException.class)
    public void missingOutput() throws IOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        victim.dispatch(new FileTaskOutput(folder.newFile()));
    }

    @Test
    public void moveIfNotSameFile() throws IOException, TaskIOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        File out = new File(folder.newFolder(), "not-existing.tmp");
        out.deleteOnExit();
        File outFile = createTemporaryBuffer();
        victim.taskOutput(outFile);
        Files.write(outFile.toPath(), new byte[] { 0, 1, 1, 1 });
        victim.dispatch(new FileTaskOutput(out));
        assertEquals(4, out.length());
    }

    @Test
    public void overwrites() throws IOException, TaskIOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.OVERWRITE, context);
        File out = folder.newFile();
        out.deleteOnExit();
        File outFile = createTemporaryBuffer();
        victim.taskOutput(outFile);
        Files.write(outFile.toPath(), new byte[] { 0, 1, 1, 1 });
        victim.dispatch(new FileTaskOutput(out));
        assertEquals(4, out.length());
        assertEquals(1, out.getParentFile().list().length);
    }

    @Test
    public void rename() throws IOException, TaskIOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.RENAME, context);
        File out = folder.newFile();
        out.deleteOnExit();
        File outFile = createTemporaryBuffer();
        victim.taskOutput(outFile);
        Files.write(outFile.toPath(), new byte[] { 0, 1, 1, 1 });
        victim.dispatch(new FileTaskOutput(out));
        assertEquals(2, out.getParentFile().list().length);
    }

    @Test(expected = IOException.class)
    public void failOnExisting() throws IOException, TaskIOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.FAIL, context);
        File out = folder.newFile();
        out.deleteOnExit();
        victim.taskOutput(out);
        File outFile = createTemporaryBuffer();
        victim.taskOutput(outFile);
        Files.write(outFile.toPath(), new byte[] { 0, 1, 1, 1 });
        victim.dispatch(new FileTaskOutput(out));
    }

    @Test(expected = IOException.class)
    public void skipBehavesLikeFails() throws IOException, TaskIOException {
        DefaultSingleOutputWriter victim = new DefaultSingleOutputWriter(ExistingOutputPolicy.SKIP, context);
        File out = folder.newFile();
        out.deleteOnExit();
        victim.taskOutput(out);
        File outFile = createTemporaryBuffer();
        victim.taskOutput(outFile);
        Files.write(outFile.toPath(), new byte[] { 0, 1, 1, 1 });
        victim.dispatch(new FileTaskOutput(out));
    }
}
