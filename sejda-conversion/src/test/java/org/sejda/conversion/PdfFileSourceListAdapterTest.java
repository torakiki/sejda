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
public class PdfFileSourceListAdapterTest {
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
        new PdfFileSourceListAdapter("/I/dont/exist");
    }

    @Test
    public void testPositive() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath());
        assertEquals(3, victim.getFileSourceList().size());
    }

    @Test
    public void testRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("test(.*).pdf");
        assertEquals(2, victim.getFileSourceList().size());
    }

    @Test
    public void testEmptyRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("");
        assertEquals(3, victim.getFileSourceList().size());
    }

    @Test(expected = ConversionException.class)
    public void testNoFile() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("NOMATCH");
        victim.getFileSourceList();
    }
}
