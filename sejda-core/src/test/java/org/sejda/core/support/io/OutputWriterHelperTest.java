/*
 * Created on 20/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

/**
 * Test unit for the {@link OutputWriterHelper}
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriterHelperTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();

    private TaskExecutionContext context;

    @Before
    public void setUp() {
        context = new TaskExecutionContext(mock(Task.class), true);
    }

    @Test
    public void copyStreamZipped() throws IOException {
        File tempFile = folder.newFile();
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputWriterHelper.copyToStreamZipped(files, out);
        assertFalse("temporary file not deleted", tempFile.exists());
        assertTrue(out.size() > 0);
    }

    @Test
    public void copyStreamSingleFile() throws IOException {
        File tempFile = folder.newFile();
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputWriterHelper.copyToStream(files.values().iterator().next(), out);
        assertFalse("temporary file not deleted", tempFile.exists());
        assertEquals(out.size(), tempFile.length());
    }

    @Test
    public void copyFailsMapSize() {
        Map<String, File> files = new HashMap<String, File>();

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);

        try {
            OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.OVERWRITE, context);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Wrong files map size"));
        }
    }

    @Test
    public void copyFailsFileType() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", folder.newFile());

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.FALSE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);

        try {
            OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.OVERWRITE, context);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().endsWith("must be a file."));
        }
    }

    @Test
    public void copyFailsOverwrite() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", folder.newFile());

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);

        try {
            OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.FAIL, context);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to write"));
        }
    }

    @Test
    public void copySingleFileSkipFallbacksToFail() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", folder.newFile());

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);

        try {
            OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.SKIP, context);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to write"));
        }
    }

    @Test
    public void copyFailsDirectoryType() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", folder.newFile());

        try {
            OutputWriterHelper.moveToDirectory(files, folder.newFile(), ExistingOutputPolicy.OVERWRITE, context);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Wrong output destination"));
        }
    }

    @Test
    public void copyDirectorySkips() throws IOException {
        File dest = folder.newFolder();
        File tempFile = folder.newFile();
        Map<String, File> files = populateWithOneExisting(dest, tempFile);
        OutputWriterHelper.moveToDirectory(files, dest, ExistingOutputPolicy.SKIP, context);
        assertEquals(2, dest.list().length);
        assertEquals(1, context.notifiableTaskMetadata().taskOutput().size());
    }

    @Test
    public void moveUnhide() throws IOException, TaskIOException {
        if (IS_OS_WINDOWS) {
            File dest = new File(folder.newFolder().getAbsolutePath(), "dest.tmp");
            File tmp = IOUtils.createTemporaryBuffer();
            IOUtils.hide(tmp.toPath());
            assertEquals(Boolean.TRUE, (Boolean) Files.getAttribute(tmp.toPath(), "dos:hidden"));
            OutputWriterHelper.moveFile(tmp, dest, ExistingOutputPolicy.FAIL, context);
            assertEquals(Boolean.FALSE, (Boolean) Files.getAttribute(dest.toPath(), "dos:hidden"));
        }
    }

    @Test
    public void copyDirectoryOverwrite() throws IOException {
        File dest = folder.newFolder();
        File tempFile = folder.newFile();
        Map<String, File> files = populateWithOneExisting(dest, tempFile);
        OutputWriterHelper.moveToDirectory(files, dest, ExistingOutputPolicy.OVERWRITE, context);
        assertEquals(2, dest.list().length);
        assertEquals(2, context.notifiableTaskMetadata().taskOutput().size());
    }

    @Test(expected = IOException.class)
    public void copyDirectoryFail() throws IOException {
        File dest = folder.newFolder();
        File tempFile = folder.newFile();
        Map<String, File> files = populateWithOneExisting(dest, tempFile);
        OutputWriterHelper.moveToDirectory(files, dest, ExistingOutputPolicy.FAIL, context);
    }

    private Map<String, File> populateWithOneExisting(File dest, File tempFile) throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);
        File existing = File.createTempFile("Chuck", "Norris", dest);
        files.put(existing.getName(), folder.newFile());
        return files;
    }

    @Test
    public void copyFailsDirectoryMkdirs() throws IOException {
        File tempFile = folder.newFile();
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isDirectory()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.FALSE);
        when(outFile.mkdirs()).thenReturn(Boolean.FALSE);

        try {
            OutputWriterHelper.moveToDirectory(files, outFile, ExistingOutputPolicy.OVERWRITE, context);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to make destination"));
        }
    }

    @Test
    public void existingOutputPolicyRENAME_conflict() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("existing.pdf", folder.newFile());

        File outFile = outputFolder.newFile("existing.pdf");
        outputFolder.newFile("existing(1).pdf");
        outputFolder.newFile("existing(2).pdf");

        OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.RENAME, context);
        assertThat(Arrays.asList(outputFolder.getRoot().list()), hasItem("existing(3).pdf"));

    }

    @Test
    public void existingOutputPolicyRENAME_noConflict() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("ok.pdf", folder.newFile());

        File outFile = new File(outputFolder.getRoot(), "ok.pdf");

        OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.RENAME, context);
        assertThat(Arrays.asList(outputFolder.getRoot().list()), hasItem("ok.pdf"));
    }

    @Test
    public void existingOutputPolicyRENAME_exception() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("existing.pdf", folder.newFile());

        File outFile = outputFolder.newFile("existing.pdf");
        for (int i = 1; i <= 100; i++) {
            outputFolder.newFile(String.format("existing(%d).pdf", i));
        }

        try {
            OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.RENAME, context);
            fail("Exception expected, about the fact that a new filename that doesn't exist could not be generated");
        } catch (IOException e) {
            assertTrue("Different exception expected, got: " + e.getMessage(),
                    e.getMessage().startsWith("Unable to generate a new filename that does not exist"));
        }
    }

    @Test
    public void moveCreatesDirectoryTree() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("file.pdf", folder.newFile());

        Path out = Paths.get(outputFolder.newFolder().getAbsolutePath(), "this", "does", "not", "exist");
        assertFalse(Files.isDirectory(out));

        OutputWriterHelper.moveToDirectory(files, out.toFile(), ExistingOutputPolicy.OVERWRITE, context);
        assertTrue(Files.isDirectory(out));
    }
}
