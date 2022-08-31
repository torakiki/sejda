/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.input.PdfFileSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class PdfFileSourceListAdapterTest {
    private Path path;

    @BeforeEach
    public void setUp(@TempDir Path folder) throws IOException {
        path = Files.createTempDirectory(folder, "testSejda");
        Files.createFile(path.resolve("1 Hello world.pdf"));
        Files.createFile(path.resolve("10 Blablabla.pdf"));
        Files.createFile(path.resolve("11 test_file.pdf"));
        Files.createFile(path.resolve("2 test_file.pdf"));
        Files.createFile(path.resolve("3 test_file.pdf"));
        Files.createFile(path.resolve("ignore_this.something"));
    }

    @Test
    public void testNegative() {
        assertThrows(ConversionException.class, () -> new PdfFileSourceListAdapter("/I/dont/exist"));
    }

    @Test
    public void testPositive() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.toAbsolutePath().toString());
        List<PdfFileSource> list = victim.getFileSourceList();
        assertEquals(5, list.size());
        assertEquals("1 Hello world.pdf", list.get(0).getName());
        assertEquals("2 test_file.pdf", list.get(1).getName());
        assertEquals("3 test_file.pdf", list.get(2).getName());
        assertEquals("10 Blablabla.pdf", list.get(3).getName());
        assertEquals("11 test_file.pdf", list.get(4).getName());
    }

    @Test
    public void testRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.toAbsolutePath().toString()).filter(
                "^(\\d+) test(.*).pdf");
        assertEquals(3, victim.getFileSourceList().size());
    }

    @Test
    public void testEmptyRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.toAbsolutePath().toString()).filter("");
        assertEquals(5, victim.getFileSourceList().size());
    }

    @Test
    public void testNoFile() {
        assertThrows(ConversionException.class,
                () -> new PdfFileSourceListAdapter(path.toAbsolutePath().toString()).filter("NOMATCH")
                        .getFileSourceList());
    }
}
