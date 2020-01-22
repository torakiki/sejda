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
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.input.PdfFileSource;

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
        folder.newFile("1 Hello world.pdf");
        folder.newFile("10 Blablabla.pdf");
        folder.newFile("11 test_file.pdf");
        folder.newFile("2 test_file.pdf");
        folder.newFile("3 test_file.pdf");
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
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath())
                .filter("^(\\d+) test(.*).pdf");
        assertEquals(3, victim.getFileSourceList().size());
    }

    @Test
    public void testEmptyRegex() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("");
        assertEquals(5, victim.getFileSourceList().size());
    }

    @Test(expected = ConversionException.class)
    public void testNoFile() {
        PdfFileSourceListAdapter victim = new PdfFileSourceListAdapter(path.getAbsolutePath()).filter("NOMATCH");
        victim.getFileSourceList();
    }
}
