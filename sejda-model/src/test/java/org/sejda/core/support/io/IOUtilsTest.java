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

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class IOUtilsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreateBuffer() throws TaskIOException {
        File tmp = IOUtils.createTemporaryBuffer();
        tmp.deleteOnExit();
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
    }

    @Test
    public void testCreateBufferWithName() throws TaskIOException {
        File tmp = IOUtils.createTemporaryBufferWithName("chuck.norris");
        tmp.deleteOnExit();
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals("chuck.norris", tmp.getName());
    }

    @Test
    public void testCreateBufferFileOut() throws TaskIOException, IOException {
        File file = folder.newFile("chuck.norris");
        FileTaskOutput out = new FileTaskOutput(file);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(file.getParent(), tmp.getParent());
    }

    @Test
    public void testCreateBufferFileOutNonExisting() throws TaskIOException {
        FileTaskOutput out = new FileTaskOutput(new File("I dont", "exist"));
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(SystemUtils.getJavaIoTmpDir().getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testCreateBufferFileOutNonExistingParentExists() throws TaskIOException, IOException {
        File dir = folder.newFolder("chuck.norris");
        FileTaskOutput out = new FileTaskOutput(new File(dir.getAbsolutePath(), "I dont exist"));
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testCreateBufferDirectoryOut() throws TaskIOException, IOException {
        File dir = folder.newFolder("chuck.norris");
        DirectoryTaskOutput out = new DirectoryTaskOutput(dir);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testCreateBufferDirectoryOutNonExisting() throws TaskIOException {
        DirectoryTaskOutput out = new DirectoryTaskOutput(new File("I dont exist"));
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(SystemUtils.getJavaIoTmpDir().getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testCreateBufferFileOrDirectoryOutNonExisting() throws TaskIOException {
        FileOrDirectoryTaskOutput out = new FileOrDirectoryTaskOutput(new File("I dont exist"));
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(SystemUtils.getJavaIoTmpDir().getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testCreateBufferFileOrDirectoryOutFile() throws TaskIOException, IOException {
        File file = folder.newFile("chuck.norris");
        FileOrDirectoryTaskOutput out = new FileOrDirectoryTaskOutput(file);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(file.getParent(), tmp.getParent());
    }

    @Test
    public void testCreateBufferFileOrDirectoryOutDir() throws TaskIOException, IOException {
        File dir = folder.newFolder();
        FileOrDirectoryTaskOutput out = new FileOrDirectoryTaskOutput(dir);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testFindNewNameThatDoesNotExist() throws Exception {
        File file = folder.newFile("chuck.norris");
        assertEquals("chuck(1).norris", IOUtils.findNewNameThatDoesNotExist(file).getName());
        folder.newFile("chuck(1).norris");
        assertEquals("chuck(2).norris", IOUtils.findNewNameThatDoesNotExist(file).getName());
    }

    // convenience method, don't pass charset and len all the time
    private String shortenFilenameBytesLength(String input) {
        return IOUtils.shortenFilenameBytesLength(input, 255, StandardCharsets.UTF_8);
    }

    private String largeFilename = repeat("aว", 300) + ".pdf";

    @Test
    public void shortenFilenameBytesLength() {
        String shorter = shortenFilenameBytesLength(largeFilename);
        assertThat(shorter, endsWith("aวaวa.pdf"));
        assertThat(shorter, startsWith("aวaว"));
        assertThat(shorter.getBytes(StandardCharsets.UTF_8).length, is(253));
    }

    @Test
    public void shortenFilenameCharLength() {
        String shorter = IOUtils.shortenFilenameCharLength(largeFilename, 255);
        assertThat(shorter, endsWith("aวaวa.pdf"));
        assertThat(shorter, startsWith("aวaว"));
        assertThat(shorter.length(), is(255));
    }

    @Test
    public void noNPEIfParentIsMissing() throws TaskIOException {
        assertNotNull(IOUtils.createTemporaryBuffer(new FileTaskOutput(new File("test.pdf"))));
    }
}
