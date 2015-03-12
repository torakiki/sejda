/*
 * Created on 06/ago/2011
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

/**
 * @author Andrea Vacondio
 * 
 */
public class SplitByOutlineLevelParametersTest {

    @Test
    public void testEquals() {
        SplitByOutlineLevelParameters eq1 = new SplitByOutlineLevelParameters(10);
        SplitByOutlineLevelParameters eq2 = new SplitByOutlineLevelParameters(10);
        SplitByOutlineLevelParameters eq3 = new SplitByOutlineLevelParameters(10);
        SplitByOutlineLevelParameters diff = new SplitByOutlineLevelParameters(1);
        diff.setOutputPrefix("prefix");
        diff.setMatchingTitleRegEx("string");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParameters() {
        SplitByOutlineLevelParameters victim = new SplitByOutlineLevelParameters(-1);
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
