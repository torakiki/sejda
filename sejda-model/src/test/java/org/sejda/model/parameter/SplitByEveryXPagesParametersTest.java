/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesParametersTest {

    @Test
    public void testEquals() {
        SplitByEveryXPagesParameters eq1 = new SplitByEveryXPagesParameters(1);
        SplitByEveryXPagesParameters eq2 = new SplitByEveryXPagesParameters(1);
        SplitByEveryXPagesParameters eq3 = new SplitByEveryXPagesParameters(1);
        SplitByEveryXPagesParameters diff = new SplitByEveryXPagesParameters(2);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void getPages() {
        SplitByEveryXPagesParameters victim = new SplitByEveryXPagesParameters(5);
        assertEquals(1, victim.getPages(5).size());
        assertEquals(2, victim.getPages(10).size());
        assertThat(victim.getPages(15), CoreMatchers.hasItems(5, 10, 15));
    }

    @Test
    public void testInvalidParameters() {
        SplitByEveryXPagesParameters victim = new SplitByEveryXPagesParameters(-5);
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
