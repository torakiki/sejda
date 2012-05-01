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
            OutputWriterHelper.copyToFile(files, outFile, true);
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
            OutputWriterHelper.copyToFile(files, outFile, true);
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
            OutputWriterHelper.copyToFile(files, outFile, false);
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
            OutputWriterHelper.copyToDirectory(files, outFile, true);
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
            OutputWriterHelper.copyToDirectory(files, outFile, true);
            fail("Exception expected");
        } catch (IOException e) {
            assertTrue("Different exception expected.", e.getMessage().startsWith("Unable to make destination"));
        }
    }
}
