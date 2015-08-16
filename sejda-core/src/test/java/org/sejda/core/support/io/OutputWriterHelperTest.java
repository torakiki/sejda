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

/**
 * Test unit for the {@link OutputWriterHelper}
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutputWriterHelperTest {

    private File tempFile;

    @Before
    public void setUp() throws IOException {
        tempFile = File.createTempFile("srcTest", "");
    }

    @Test
    public void testExecuteCopyStream() throws IOException {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputWriterHelper.copyToStream(files, out);
        assertFalse("temporary file not deleted", tempFile.exists());
        assertTrue(out.size() > 0);
    }

    @Test
    public void testExecuteCopyFailsMapSize() {
        Map<String, File> files = new HashMap<String, File>();

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);

        try {
            OutputWriterHelper.moveToFile(files, outFile, true);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Wrong files map size"));
        }
    }

    @Test
    public void testExecuteCopyFailsFileType() {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.FALSE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);

        try {
            OutputWriterHelper.moveToFile(files, outFile, true);
            fail("Exception expected");
        } catch (IOException e) {
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

        try {
            OutputWriterHelper.moveToFile(files, outFile, false);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to overwrite the"));
        }
    }

    @Test
    public void testExecuteCopyFailsDirectoryType() {
        Map<String, File> files = new HashMap<String, File>();
        files.put("newName", tempFile);

        File outFile = mock(File.class);
        when(outFile.isDirectory()).thenReturn(Boolean.FALSE);

        try {
            OutputWriterHelper.moveToDirectory(files, outFile, true);
            fail("Exception expected");
        } catch (IOException e) {
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

        try {
            OutputWriterHelper.moveToDirectory(files, outFile, true);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to make destination"));
        }
    }
}
