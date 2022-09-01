/*
 * Created on 19 ott 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.conversion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.conversion.exception.ConversionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class WildcardsPdfFileSourceAdapterTest {

    @TempDir
    public static Path folder;

    private static Path path;

    @BeforeAll
    public static void setUp() throws IOException {
        path = Files.createTempDirectory(folder, "sejda");
        Files.createFile(path.resolve("7.pdf"));
        Files.createFile(path.resolve("44.pdf"));
        Files.createFile(path.resolve("14.pdf"));
        Files.createFile(path.resolve("ignore_this.something"));
    }

    @Test
    public void testNegative() {
        assertThrows(ConversionException.class, () -> new WildcardsPdfFileSourceAdapter("/I/dont/exist"));
    }

    @Test
    public void testNegativeWildcardNonExistingParent() {
        assertThrows(ConversionException.class, () -> new WildcardsPdfFileSourceAdapter("/I/dont/exist/*.pdf"));
    }

    @Test
    public void testPositive() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(path.toAbsolutePath() + "/*.pdf");
        assertEquals(3, victim.getPdfFileSources().size());
    }

    @Test
    public void testPositiveCaseInsensitive() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(path.toAbsolutePath() + "/*.PdF");
        assertEquals(3, victim.getPdfFileSources().size());
    }

    @Test
    public void testPositiveFilePath() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(path.toAbsolutePath() + "/14.pdf");
        assertEquals(1, victim.getPdfFileSources().size());
    }

    @Test
    public void testOrder() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(path.toAbsolutePath() + "/*.pdf");
        assertEquals(3, victim.getPdfFileSources().size());
        assertEquals("7.pdf", victim.getPdfFileSources().get(0).getName());
        assertEquals("14.pdf", victim.getPdfFileSources().get(1).getName());
        assertEquals("44.pdf", victim.getPdfFileSources().get(2).getName());
    }
}
