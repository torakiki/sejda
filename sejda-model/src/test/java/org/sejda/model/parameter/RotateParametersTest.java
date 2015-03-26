/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;
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
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
