/*
 * Created on 26 ago 2016
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

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.nup.PageOrder;
import org.sejda.model.output.DirectoryTaskOutput;

/**
 * @author Andrea Vacondio
 *
 */
public class NupParametersTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testEquals() {
        NupParameters eq1 = new NupParameters(3, PageOrder.HORIZONTAL);
        NupParameters eq2 = new NupParameters(3, PageOrder.HORIZONTAL);
        NupParameters eq3 = new NupParameters(3, PageOrder.HORIZONTAL);
        NupParameters diff = new NupParameters(3, PageOrder.VERTICAL);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void invalidPageOrder() throws IOException {
        NupParameters victim = new NupParameters(3, null);
        victim.addSource(PdfFileSource.newInstanceNoPassword(tmpFolder.newFile("test.pdf")));
        victim.setOutput(new DirectoryTaskOutput(tmpFolder.newFolder()));
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void invalidN() throws IOException {
        NupParameters victim = new NupParameters(-4, PageOrder.HORIZONTAL);
        victim.addSource(PdfFileSource.newInstanceNoPassword(tmpFolder.newFile("test.pdf")));
        victim.setOutput(new DirectoryTaskOutput(tmpFolder.newFolder()));
        TestUtils.assertInvalidParameters(victim);
    }
}
