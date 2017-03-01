/*
 * Created on 20 gen 2016
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
package org.sejda.model.parameter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 *
 */
public class AddBackPagesParametersTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEquals() {
        AddBackPagesParameters eq1 = new AddBackPagesParameters();
        AddBackPagesParameters eq2 = new AddBackPagesParameters();
        AddBackPagesParameters eq3 = new AddBackPagesParameters();
        AddBackPagesParameters diff = new AddBackPagesParameters();
        diff.addPageRange(new PageRange(12));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptySourceList() throws IOException {
        AddBackPagesParameters victim = new AddBackPagesParameters();
        victim.addPageRange(new PageRange(2));
        victim.setBackPagesSource(PdfFileSource.newInstanceNoPassword(folder.newFile("source.pdf")));
        victim.setOutput(new FileOrDirectoryTaskOutput(folder.newFolder()));
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void getPagesEmptyRange() {
        AddBackPagesParameters victim = new AddBackPagesParameters();
        assertEquals(10, victim.getPages(10).size());
    }

    @Test
    public void getPagesRange() {
        AddBackPagesParameters victim = new AddBackPagesParameters();
        victim.addPageRange(new PageRange(2, 5));
        assertEquals(4, victim.getPages(10).size());
    }
}
