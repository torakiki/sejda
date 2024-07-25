/*
 * Created on Oct 13, 2011
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.conversion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link PdfFileSourceAdapter}
 *
 * @author Eduard Weissmann
 */
public class PdfFileSourceAdapterTest {

    @TempDir
    public Path folder;

    private Path file;

    @BeforeEach
    public void setUp() throws IOException {
        file = folder.resolve("inputFile1.pdf");
        Files.copy(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf"), file);
    }

    @Test
    public void windowsFileWithPassword() {
        var filePath = file.toAbsolutePath().toString();
        PdfFileSource result = new PdfFileSourceAdapter(filePath + ":secret123").getPdfFileSource();
        assertThat(result.getPassword(), is("secret123"));
        assertThat(result.getSource(), is(new File(filePath)));
    }

    @Test
    public void windowsFileNoPassword() {
        var filePath = file.toAbsolutePath().toString();
        PdfFileSource result = new PdfFileSourceAdapter(filePath).getPdfFileSource();
        assertNull(result.getPassword());
        assertThat(result.getSource(), is(new File(filePath)));
    }

    @Test
    public void protectedFileWithPasswordContainingSeparator() {
        var filePath = file.toAbsolutePath().toString();
        PdfFileSource result = new PdfFileSourceAdapter(filePath + ":secret.pdf:password").getPdfFileSource();
        assertThat(result.getPassword(), is("secret.pdf:password"));
        assertThat(result.getSource(), is(new File(filePath)));
    }

    @Test
    public void fileDoestExist() {
        var e = assertThrows(SejdaRuntimeException.class,
                () -> new PdfFileSourceAdapter("/tmp/doesntexist.pdf:secret").getPdfFileSource());
        assertThat(e.getMessage(), containsString("does not exist"));
    }

}
