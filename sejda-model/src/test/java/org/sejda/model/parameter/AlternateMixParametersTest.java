/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixParametersTest {

    private SingleTaskOutput<?> output;

    @Before
    public void setUp() {
        File outFile = mock(File.class);
        output = new FileTaskOutput(outFile);
    }

    @Test
    public void testEquals() {
        PdfMixInput firstInput = mock(PdfMixInput.class);
        PdfMixInput secondInput = mock(PdfMixInput.class);
        AlternateMixParameters eq1 = new AlternateMixParameters(firstInput, secondInput);
        eq1.setOutputName("name.pdf");
        AlternateMixParameters eq2 = new AlternateMixParameters(firstInput, secondInput);
        eq2.setOutputName("name.pdf");
        AlternateMixParameters eq3 = new AlternateMixParameters(firstInput, secondInput);
        eq3.setOutputName("name.pdf");
        AlternateMixParameters diff = new AlternateMixParameters(firstInput, secondInput);
        diff.setOutputName("diff.pdf");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersNullInput() {
        InputStream stream = mock(InputStream.class);
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "source.pdf");
        PdfMixInput input = new PdfMixInput(source, false, 1);
        AlternateMixParameters victim = new AlternateMixParameters(input, null);
        victim.setOutputName("name.pdf");
        victim.setOutput(output);

        TestUtils.assertInvalidParameters(victim);
        AlternateMixParameters victim2 = new AlternateMixParameters(null, input);
        victim2.setOutputName("name.pdf");
        victim2.setOutput(output);
        TestUtils.assertInvalidParameters(victim2);

        AlternateMixParameters victim3 = new AlternateMixParameters(null, null);
        victim3.setOutputName("name.pdf");
        TestUtils.assertInvalidParameters(victim3);
    }

    @Test
    public void testInvalidParametersNullSource() {
        PdfMixInput input = new PdfMixInput(null, false, 1);
        AlternateMixParameters victim = new AlternateMixParameters(input, input);
        victim.setOutputName("name.pdf");
        victim.setOutput(output);
        TestUtils.assertInvalidParameters(victim);
    }
}
