/*
 * Created on 06/set/2015
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
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.pdf.form.AcroFormPolicy;

/**
 * @author Andrea Vacondio
 *
 */
public class CombineReorderParametersTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEquals() {
        CombineReorderParameters eq1 = new CombineReorderParameters();
        eq1.addPage(1, 5);
        CombineReorderParameters eq2 = new CombineReorderParameters();
        eq2.addPage(1, 5);
        CombineReorderParameters eq3 = new CombineReorderParameters();
        eq3.addPage(1, 5);
        CombineReorderParameters diff = new CombineReorderParameters();
        diff.addPage(2, 5);
        diff.setAcroFormPolicy(AcroFormPolicy.MERGE);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptyList() throws IOException {
        CombineReorderParameters victim = new CombineReorderParameters();
        victim.setOutput(new FileTaskOutput(folder.newFile("out.pdf")));
        victim.addSources(Arrays.asList(PdfFileSource.newInstanceNoPassword(folder.newFile("chuck.pdf"))));
        TestUtils.assertInvalidParameters(victim);
    }
}
