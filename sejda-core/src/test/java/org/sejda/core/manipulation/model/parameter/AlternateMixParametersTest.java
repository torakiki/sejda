/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.manipulation.model.input.PdfMixInput;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.output.TaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixParametersTest {

    private TaskOutput output;

    @Before
    public void setUp() {
        output = mock(TaskOutput.class);
    }

    @Test
    public void testEquals() {
        PdfMixInput firstInput = mock(PdfMixInput.class);
        PdfMixInput secondInput = mock(PdfMixInput.class);
        AlternateMixParameters eq1 = new AlternateMixParameters(firstInput, secondInput, "name");
        AlternateMixParameters eq2 = new AlternateMixParameters(firstInput, secondInput, "name");
        AlternateMixParameters eq3 = new AlternateMixParameters(firstInput, secondInput, "name");
        AlternateMixParameters diff = new AlternateMixParameters(firstInput, secondInput, "diffName");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersNullInput() {
        InputStream stream = mock(InputStream.class);
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "source.pdf");
        PdfMixInput input = new PdfMixInput(source, false, 1);
        AlternateMixParameters victim = new AlternateMixParameters(input, null, "name.pdf");

        victim.setOutput(output);
        TestUtils.assertInvalidParameters(victim);
        AlternateMixParameters victim2 = new AlternateMixParameters(null, input, "name.pdf");

        victim2.setOutput(output);
        TestUtils.assertInvalidParameters(victim2);

        AlternateMixParameters victim3 = new AlternateMixParameters(null, null, "name.pdf");
        TestUtils.assertInvalidParameters(victim3);
    }

    @Test
    public void testInvalidParametersNullSource() {
        PdfMixInput input = new PdfMixInput(null, false, 1);
        AlternateMixParameters victim = new AlternateMixParameters(input, input, "name.pdf");

        victim.setOutput(output);
        TestUtils.assertInvalidParameters(victim);
    }
}
