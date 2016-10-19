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

import org.junit.Before;
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
    private File path;

    @Before
    public void setUp() throws IOException {
        folder.newFile("test_file.pdf");
        folder.newFile("test2_file.pdf");
        folder.newFile("another_test_file.pdf");
        folder.newFile("ignore_this.something");
        this.path = folder.getRoot();
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
    public void testPositive() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(path.getAbsolutePath() + "/*.pdf");
        assertEquals(3, victim.getPdfFileSources().size());
    }

    @Test
    public void testPositiveCaseInsensitive() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(path.getAbsolutePath() + "/*.PdF");
        assertEquals(3, victim.getPdfFileSources().size());
    }

    @Test
    public void testPositiveFilePath() {
        WildcardsPdfFileSourceAdapter victim = new WildcardsPdfFileSourceAdapter(
                path.getAbsolutePath() + "/test_file.pdf");
        assertEquals(1, victim.getPdfFileSources().size());
    }
}
