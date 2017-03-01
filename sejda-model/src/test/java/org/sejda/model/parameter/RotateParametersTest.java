/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.SingleOrMultipleTaskOutput;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;

/**
 * @author Andrea Vacondio
 * 
 */
public class RotateParametersTest {

    @Test
    public void testEquals() {
        RotateParameters eq1 = new RotateParameters(Rotation.DEGREES_0, PredefinedSetOfPages.ALL_PAGES);
        RotateParameters eq2 = new RotateParameters(Rotation.DEGREES_0, PredefinedSetOfPages.ALL_PAGES);
        RotateParameters eq3 = new RotateParameters(Rotation.DEGREES_0, PredefinedSetOfPages.ALL_PAGES);
        RotateParameters diff = new RotateParameters(Rotation.DEGREES_0, PredefinedSetOfPages.ALL_PAGES);
        diff.setOutputPrefix("prefix");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParameters() {
        RotateParameters victim = new RotateParameters(null, null);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
