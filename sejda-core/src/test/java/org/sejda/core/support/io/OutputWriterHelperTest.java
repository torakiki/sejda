/*
 * Created on 20/giu/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test unit for the {@link OutputWriterHelper}
 *
 * @author Andrea Vacondio
 */
public class OutputWriterHelperTest {

    private TaskExecutionContext context;

    @BeforeEach
    public void setUp() {
        context = new TaskExecutionContext(mock(Task.class), true);
    }

    @Test
    public void copyStreamZipped(@TempDir Path folder) throws IOException {
        var tempFile = Files.createTempFile(folder, null, null).toFile();
        Map<String, File> files = Map.of("newName", tempFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputWriterHelper.copyToStreamZipped(files, out);
        assertFalse(tempFile.exists(), "temporary file not deleted");
        assertTrue(out.size() > 0);
    }

    @Test
    public void copyStreamSingleFile(@TempDir Path folder) throws IOException {
        var tempFile = Files.createTempFile(folder, null, null).toFile();
        Map<String, File> files = Map.of("newName", tempFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputWriterHelper.copyToStream(files.values().iterator().next(), out);
        assertFalse(tempFile.exists(), "temporary file not deleted");
        assertEquals(out.size(), tempFile.length());
    }

    @Test
    public void copyFailsMapSize(@TempDir Path folder) throws IOException {
        Map<String, File> files = new HashMap<>();
        var outFile = Files.createTempFile(folder, null, null).toFile();
        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.OVERWRITE, context));
        assertThat(e.getMessage(), containsString("Wrong files map size"));
    }

    @Test
    public void copyFailsFileType(@TempDir Path folder) throws IOException {
        Map<String, File> files = Map.of("newName", Files.createTempFile(folder, null, null).toFile());
        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.FALSE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);
        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.OVERWRITE, context));
        assertThat(e.getMessage(), containsString("must be a file"));
    }

    @Test
    public void copyFailsOverwrite(@TempDir Path folder) throws IOException {
        Map<String, File> files = Map.of("newName", Files.createTempFile(folder, null, null).toFile());

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);
        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.FAIL, context));
        assertThat(e.getMessage(), containsString("Unable to write"));
    }

    @Test
    public void copySingleFileSkipFallbacksToFail(@TempDir Path folder) throws IOException {
        Map<String, File> files = Map.of("newName", Files.createTempFile(folder, null, null).toFile());

        File outFile = mock(File.class);
        when(outFile.isFile()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.TRUE);
        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.SKIP, context));
        assertThat(e.getMessage(), containsString("Unable to write"));
    }

    @Test
    public void copyFailsDirectoryType(@TempDir Path folder) throws IOException {
        Map<String, File> files = Map.of("newName", Files.createTempFile(folder, null, null).toFile());

        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToDirectory(files, Files.createTempFile(folder, null, null).toFile(),
                        ExistingOutputPolicy.OVERWRITE, context));
        assertThat(e.getMessage(), containsString("Wrong output destination"));
    }

    @Test
    public void copyDirectorySkips(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var tempFile = Files.createTempFile(folder, null, null).toFile();
        Map<String, File> files = populateWithOneExisting(dest, tempFile, folder);
        OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.SKIP, context);
        assertEquals(2, dest.toFile().list().length);
        assertEquals(1, context.notifiableTaskMetadata().taskOutput().size());
        assertEquals(1, context.notifiableTaskMetadata().skippedOutput().size());
    }

    @Test
    public void copyDirectoryOverwrite(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var tempFile = Files.createTempFile(folder, null, null).toFile();
        Map<String, File> files = populateWithOneExisting(dest, tempFile, folder);
        OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.OVERWRITE, context);
        assertEquals(2, dest.toFile().list().length);
        assertEquals(2, context.notifiableTaskMetadata().taskOutput().size());
    }

    @Test
    public void copyDirectoryFail(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var tempFile = Files.createTempFile(folder, null, null).toFile();
        Map<String, File> files = populateWithOneExisting(dest, tempFile, folder);
        assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.FAIL, context));
    }

    private Map<String, File> populateWithOneExisting(Path dest, File tempFile, Path tempFolder) throws IOException {
        Map<String, File> files = new HashMap<>();
        files.put("newName", tempFile);
        var existing = Files.createTempFile(dest, "Chuck", "Norris").toFile();
        files.put(existing.getName(), Files.createTempFile(tempFolder, null, null).toFile());
        return files;
    }

    @Test
    public void updateFilname(@TempDir Path folder) throws IOException {
        Map<String, File> files = new LinkedHashMap<>();
        var dest = Files.createTempDirectory(folder, "sejda");
        files.put("1_of_[TOTAL_FILESNUMBER].pdf", Files.createTempFile(dest, "Chuck", "Norris").toFile());
        files.put("2_of_[TOTAL_FILESNUMBER].pdf", Files.createTempFile(dest, "Chuck", "Norris").toFile());
        OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.SKIP, context);
        assertEquals(2, dest.toFile().list().length);
        assertEquals(2, context.notifiableTaskMetadata().taskOutput().size());
        assertEquals(0, context.notifiableTaskMetadata().skippedOutput().size());
        assertEquals(2, context.notifiableTaskMetadata().taskOutput().size());
        assertEquals("1_of_2.pdf", context.notifiableTaskMetadata().taskOutput().get(0).getName());
        assertEquals("2_of_2.pdf", context.notifiableTaskMetadata().taskOutput().get(1).getName());
    }

    @Test
    public void namesAreUpdatedBeforeShorten(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("[TOTAL_FILESNUMBER]" + repeat("a", 248) + "[TOTAL_FILESNUMBER].pdf",
                Files.createTempFile(dest, null, null).toFile());
        OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.SKIP, context);
        assertEquals(1, dest.toFile().list().length);
        assertEquals(1, context.notifiableTaskMetadata().taskOutput().size());
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName(), startsWith("1"));
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName(), endsWith("1.pdf"));
    }

    @Test
    public void namesAreShortened(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("[TOTAL_FILESNUMBER]" + repeat("a", 400) + "[TOTAL_FILESNUMBER].pdf",
                Files.createTempFile(dest, null, null).toFile());
        OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.SKIP, context);
        assertEquals(1, dest.toFile().list().length);
        assertEquals(1, context.notifiableTaskMetadata().taskOutput().size());
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName(), startsWith("1"));
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName(), endsWith("a.pdf"));
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName().length(), is(lessThan(256)));
    }

    @Test
    public void namesAreShortenedUnicode(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("[TOTAL_FILESNUMBER]" + repeat("ว", 400) + "[TOTAL_FILESNUMBER].pdf",
                Files.createTempFile(dest, null, null).toFile());
        OutputWriterHelper.moveToDirectory(files, dest.toFile(), ExistingOutputPolicy.SKIP, context);
        assertEquals(1, dest.toFile().list().length);
        assertEquals(1, context.notifiableTaskMetadata().taskOutput().size());
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName(), startsWith("1"));
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName(), endsWith("ว.pdf"));
        assertThat(context.notifiableTaskMetadata().taskOutput().get(0).getName().length(), is(lessThan(256)));
    }

    @Test
    public void copyFailsDirectoryMkdirs(@TempDir Path folder) throws IOException {
        var files = Map.of("newName", Files.createTempFile(folder, null, null).toFile());

        File outFile = mock(File.class);
        when(outFile.isDirectory()).thenReturn(Boolean.TRUE);
        when(outFile.exists()).thenReturn(Boolean.FALSE);
        when(outFile.mkdirs()).thenReturn(Boolean.FALSE);

        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToDirectory(files, outFile, ExistingOutputPolicy.OVERWRITE, context));
        assertThat(e.getMessage(), containsString("Unable to make destination"));
    }

    @Test
    public void existingOutputPolicyRENAME_conflict(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("existing.pdf", Files.createTempFile(folder, null, null).toFile());

        File outFile = Files.createFile(dest.resolve("existing.pdf")).toFile();
        Files.createFile(dest.resolve("existing(1).pdf"));
        Files.createFile(dest.resolve("existing(2).pdf"));

        OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.RENAME, context);
        assertEquals(4, Files.list(dest).count());
        assertEquals(1,
                Files.list(dest).map(Path::getFileName).map(Path::toString).filter(n -> n.equals("existing(3).pdf"))
                        .count());

    }

    @Test
    public void existingOutputPolicyRENAME_noConflict(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("ok.pdf", Files.createTempFile(folder, null, null).toFile());

        var outFile = dest.resolve("ok.pdf");

        OutputWriterHelper.moveToFile(files, outFile.toFile(), ExistingOutputPolicy.RENAME, context);
        assertEquals(1, Files.list(dest).count());
        assertEquals(1,
                Files.list(dest).map(Path::getFileName).map(Path::toString).filter(n -> n.equals("ok.pdf")).count());
    }

    @Test
    public void existingOutputPolicyRENAME_exception(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("existing.pdf", Files.createTempFile(folder, null, null).toFile());

        File outFile = Files.createFile(dest.resolve("existing.pdf")).toFile();
        for (int i = 1; i <= 100; i++) {
            Files.createFile(dest.resolve(String.format("existing(%d).pdf", i)));
        }

        var e = assertThrows(IOException.class,
                () -> OutputWriterHelper.moveToFile(files, outFile, ExistingOutputPolicy.RENAME, context));
        assertThat(e.getMessage(), containsString("Unable to generate a new filename that does not exist"));
    }

    @Test
    public void moveCreatesDirectoryTree(@TempDir Path folder) throws IOException {
        var dest = Files.createTempDirectory(folder, "sejda");
        var files = Map.of("file.pdf", Files.createTempFile(folder, null, null).toFile());

        Path out = dest.resolve("this/does/not/exist");
        assertFalse(Files.isDirectory(out));

        OutputWriterHelper.moveToDirectory(files, out.toFile(), ExistingOutputPolicy.OVERWRITE, context);
        assertTrue(Files.isDirectory(out));
    }
}
