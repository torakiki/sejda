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
package org.sejda.model.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;

/**
 * @author Andrea Vacondio
 */
public class IOUtilsTest {
    @TempDir
    public Path folder;

    @Test
    public void testCreateBuffer() throws TaskIOException {
        File tmp = IOUtils.createTemporaryBuffer();
        tmp.deleteOnExit();
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertThat(tmp.getName(), startsWith(IOUtils.BUFFER_NAME));
    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    public void customPrefix() throws TaskIOException {
        System.setProperty(IOUtils.TMP_BUFFER_PREFIX_PROPERTY_NAME, "chuck");
        File tmp = IOUtils.createTemporaryBuffer();
        tmp.deleteOnExit();
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertThat(tmp.getName(), startsWith("chuck"));
        System.clearProperty(IOUtils.TMP_BUFFER_PREFIX_PROPERTY_NAME);
    }

    @Test
    public void testCreateFolder() throws TaskIOException {
        File tmp = IOUtils.createTemporaryFolder();
        tmp.deleteOnExit();
        assertTrue(tmp.exists());
        assertTrue(tmp.isDirectory());
        assertThat(tmp.getName(), startsWith(IOUtils.BUFFER_NAME));
    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    public void customPrefixFolder() {
        System.setProperty(IOUtils.TMP_BUFFER_PREFIX_PROPERTY_NAME, "chuck");
        File tmp = IOUtils.createTemporaryFolder();
        tmp.deleteOnExit();
        assertTrue(tmp.exists());
        assertTrue(tmp.isDirectory());
        assertThat(tmp.getName(), startsWith("chuck"));
        System.clearProperty(IOUtils.TMP_BUFFER_PREFIX_PROPERTY_NAME);
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
        var file = Files.createTempFile(folder, "chuck", "norris").toFile();
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
        var dir = Files.createTempDirectory(folder, null).toFile();
        var out = new FileTaskOutput(new File(dir.getAbsolutePath(), "I dont exist"));
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testCreateBufferDirectoryOut() throws TaskIOException, IOException {
        var dir = Files.createTempDirectory(folder, null).toFile();
        DirectoryTaskOutput out = new DirectoryTaskOutput(dir);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    public void testCreateBufferDirectoryOutCustomPrefix() throws TaskIOException, IOException {
        System.setProperty(IOUtils.TMP_BUFFER_PREFIX_PROPERTY_NAME, "chuck");
        var dir = Files.createTempDirectory(folder, null).toFile();
        DirectoryTaskOutput out = new DirectoryTaskOutput(dir);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
        assertThat(tmp.getName(), containsString("chuck"));
        System.clearProperty(IOUtils.TMP_BUFFER_PREFIX_PROPERTY_NAME);
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
        var file = Files.createTempFile(folder, "chuck", "norris").toFile();
        FileOrDirectoryTaskOutput out = new FileOrDirectoryTaskOutput(file);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(file.getParent(), tmp.getParent());
    }

    @Test
    public void testCreateBufferFileOrDirectoryOutDir() throws TaskIOException, IOException {
        var dir = Files.createTempDirectory(folder, null).toFile();
        FileOrDirectoryTaskOutput out = new FileOrDirectoryTaskOutput(dir);
        File tmp = IOUtils.createTemporaryBuffer(out);
        assertTrue(tmp.exists());
        assertTrue(tmp.isFile());
        assertEquals(dir.getAbsolutePath(), tmp.getParent());
    }

    @Test
    public void testFindNewNameThatDoesNotExist() throws Exception {
        var file = Files.createFile(folder.resolve("chuck.norris")).toFile();
        assertEquals("chuck(1).norris", IOUtils.findNewNameThatDoesNotExist(file).getName());
        Files.createFile(folder.resolve("chuck(1).norris"));
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

    @Test
    public void nullSafe() {
        assertEquals("", IOUtils.toSafeFilename(null));
    }

    @Test
    public void testSafeFilename() {
        assertEquals("1_Invoice#0001", IOUtils.toSafeFilename("1_Invoice#0001:*<>/\\"));
        assertEquals("..test", IOUtils.toSafeFilename("../test"));
        assertEquals("..test", IOUtils.toSafeFilename("..\\test"));
        assertEquals(".test", IOUtils.toSafeFilename("./test"));
        assertEquals("rest", IOUtils.toSafeFilename("\r\n\t\frest"));
    }

    @Test
    public void safeFilenameWhitespaces() {
        assertEquals("Chuck Norris", IOUtils.toSafeFilename("Chuck\tNorris"));
        assertEquals("Chuck Norris", IOUtils.toSafeFilename("\u00A0Chuck\u00A0Norris\u00A0"));
        assertEquals("Chuck Norris", IOUtils.toSafeFilename("\u00A0\n\t\u000B\f\rChuck\nNorris\u202f"));
        assertEquals("This is a Chuck Norris roundkick, will Steven Segal survive Nope!", IOUtils.toSafeFilename(
                "This\u1680is\u180ea\u2000Chuck\u200aNorris\u202froundkick,\u205fwill\u3000Steven\fSegal\rsurvive?\u000BNope!"));
    }

    @Test
    public void testStrictFilename_specialChars() throws TaskIOException {
        assertEquals("1_Invoice0001", IOUtils.toStrictFilename("1_Invoice#0001:*<>/\\"));
    }

    @Test
    public void testStrictFilename_tooLong() throws TaskIOException {
        String tooLong = StringUtils.repeat('a', 256);
        
        // check we can actually create the file, it's not too long
        File tmpFile = IOUtils.createTemporaryBufferWithName(IOUtils.toStrictFilename(tooLong) + ".pdf");
        assertTrue(tmpFile.exists());
        tmpFile.delete();

        assertEquals(StringUtils.repeat('a', 251), IOUtils.toStrictFilename(tooLong));
    }

    @Test
    public void testNulls() {
        assertEquals("", IOUtils.toSafeFilename(null));
        assertEquals("", IOUtils.toStrictFilename(null));
    }
}
