/*
 * Created on 20/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.support.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.output.PdfDirectoryOutput;
import org.sejda.core.manipulation.model.output.PdfFileOutput;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.support.io.model.OutputDestination;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test unit for the {@link OutputWriter}
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriterTest {

    private File tempFile;

    @Before
    public void setUp() throws IOException {
        tempFile = File.createTempFile("srcTest", "");
    }

    @Test
    public void testExecuteCopyFile() throws TaskIOException, IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = File.createTempFile("outTemp", "");
        PdfFileOutput output = PdfFileOutput.newInstance(outFile);

        OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output).overwriting(true));
        assertFalse("temporary file not deleted", tempFile.exists());
    }

    @Test
    public void testExecuteCopyStream() throws TaskIOException, IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        PdfStreamOutput output = PdfStreamOutput.newInstance(new ByteArrayOutputStream());
        OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output));
        assertFalse("temporary file not deleted", tempFile.exists());
    }

    @Test
    public void testExecuteCopyFailsMapSize() {
        Map<String, File> files = new HashMap<String, File>();

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        PdfFileOutput output = PdfFileOutput.newInstance(outFile);

        try {
            OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output).overwriting(true));
            fail("Exception expected");
        } catch (TaskIOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Wrong files map size"));
        }
    }

    @Test
    public void testExecuteCopyFailsFileType() {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        PdfFileOutput output = PdfFileOutput.newInstance(outFile);
        when(outFile.isFile()).thenReturn(Boolean.FALSE);

        try {
            OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output).overwriting(true));
            fail("Exception expected");
        } catch (TaskIOException e) {
            assertTrue("Different exception expected.", e.getMessage().endsWith("must be a file."));
        }
    }

    @Test
    public void testExecuteCopyFailsOverwrite() {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);
        PdfFileOutput output = PdfFileOutput.newInstance(outFile);

        try {
            OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output).overwriting(false));
            fail("Exception expected");
        } catch (TaskIOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to overwrite the"));
        }
    }

    @Test
    public void testExecuteCopyFailsDirectoryType() {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isDirectory()).thenReturn(Boolean.TRUE);
        PdfDirectoryOutput output = PdfDirectoryOutput.newInstance(outFile);
        when(outFile.isDirectory()).thenReturn(Boolean.FALSE);

        try {
            OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output).overwriting(true));
            fail("Exception expected");
        } catch (TaskIOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Wrong output destination"));
        }
    }

    @Test
    public void testExecuteCopyFailsDirectoryMkdirs() {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isDirectory()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.FALSE);
        when(outFile.mkdirs()).thenReturn(Boolean.FALSE);
        PdfDirectoryOutput output = PdfDirectoryOutput.newInstance(outFile);

        try {
            OutputWriter.executeCopyAndDelete(files, OutputDestination.destination(output).overwriting(true));
            fail("Exception expected");
        } catch (TaskIOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to make destination"));
        }
    }
}
