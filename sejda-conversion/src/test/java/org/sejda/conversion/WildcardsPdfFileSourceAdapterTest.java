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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.conversion.exception.ConversionException;

/**
 * @author Andrea Vacondio
 *
 */
public class WildcardsPdfFileSourceAdapterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File setUpFiles() throws IOException {
        folder.newFile("test_file.pdf");
        folder.newFile("test2_file.pdf");
        folder.newFile("another_test_file.pdf");
        folder.newFile("ignore_this.something");
        return folder.getRoot();
    }

    @Test(expected = ConversionException.class)
    public void testNegative() {
        new WildcardsPdfFileSourceAdapter("/I/dont/exist");
    }

    @Test(expected = ConversionException.class)
    public void testNegativeWildcardNonExistingParent() {
        new WildcardsPdfFileSourceAdapter("/I/dont/exist/*.pdf");
    }

    @Test
    public void testPositive() throws IOException {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(
                setUpFiles().getAbsolutePath() + "/*.pdf");
        assertEquals(3, victim.getPdfFileSources().size());
    }

    @Test
    public void testPositiveCaseInsensitive() throws IOException {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(
                setUpFiles().getAbsolutePath() + "/*.PdF");
        assertEquals(3, victim.getPdfFileSources().size());
    }

    @Test
    public void testPositiveFilePath() throws IOException {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(
                setUpFiles().getAbsolutePath() + "/test_file.pdf");
        assertEquals(1, victim.getPdfFileSources().size());
    }

    @Test
    public void testOrder() throws IOException {
        folder.newFile("7.pdf");
        folder.newFile("44.pdf");
        folder.newFile("14.pdf");
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(
                folder.getRoot().getAbsolutePath() + "/*.pdf");
        assertEquals(3, victim.getPdfFileSources().size());
        assertEquals("7.pdf", victim.getPdfFileSources().get(0).getName());
        assertEquals("14.pdf", victim.getPdfFileSources().get(1).getName());
        assertEquals("44.pdf", victim.getPdfFileSources().get(2).getName());
    }
}
