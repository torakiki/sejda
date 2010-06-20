/*
 * Created on 20/giu/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.support.io.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

/**
 * Test unit for the {@link OutputWriter}
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriterTest {

    private OutputWriter victim;
    private File tempFile;

    @Before
    public void setUp() throws IOException {
        victim = new OutputWriter();
        tempFile = File.createTempFile("srcTest", "");
    }

    @Test
    public void testExecuteCopyFile() throws TaskIOException, IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = File.createTempFile("outTemp", "");
        PdfFileOutput output = new PdfFileOutput(outFile);

        victim.executeCopy(files, OutputDestination.destination(output).overwriting(true));
        assertFalse("temporary file not deleted", tempFile.exists());
    }
    @Test
    public void testExecuteCopyStream() throws TaskIOException, IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        PdfStreamOutput output = new PdfStreamOutput(new ByteArrayOutputStream());
        victim.executeCopy(files, OutputDestination.destination(output));
        assertFalse("temporary file not deleted", tempFile.exists());
    }

    @Test
    public void testExecuteCopyFailsMapSize() {
        Map<String, File> files = new HashMap<String, File>();

        File outFile = mock(File.class);
        PdfFileOutput output = new PdfFileOutput(outFile);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);

        try {
            victim.executeCopy(files, OutputDestination.destination(output).overwriting(true));
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
        PdfFileOutput output = new PdfFileOutput(outFile);
        when(outFile.isFile()).thenReturn(Boolean.FALSE);

        try {
            victim.executeCopy(files, OutputDestination.destination(output).overwriting(true));
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
        PdfFileOutput output = new PdfFileOutput(outFile);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);
        
        try {
            victim.executeCopy(files, OutputDestination.destination(output).overwriting(false));
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
        PdfDirectoryOutput output = new PdfDirectoryOutput(outFile);
        when(outFile.isDirectory()).thenReturn(Boolean.FALSE);

        try {
            victim.executeCopy(files, OutputDestination.destination(output).overwriting(true));
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
        PdfDirectoryOutput output = new PdfDirectoryOutput(outFile);
        when(outFile.isDirectory()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.FALSE);
        when(outFile.mkdirs()).thenReturn(Boolean.FALSE);

        try {
            victim.executeCopy(files, OutputDestination.destination(output).overwriting(true));
            fail("Exception expected");
        } catch (TaskIOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to make destination"));
        }
    }
}
