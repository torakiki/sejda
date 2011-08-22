/*
 * Created on 18/ago/2011
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

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.manipulation.model.output.TaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class UnpackParametersTest {

    private TaskOutput output;

    @Before
    public void setUp() {
        output = mock(TaskOutput.class);
    }

    @Test
    public void testEquals() {
        UnpackParameters eq1 = new UnpackParameters(output);
        UnpackParameters eq2 = new UnpackParameters(output);
        UnpackParameters eq3 = new UnpackParameters(output);
        UnpackParameters diff = new UnpackParameters(output);
        diff.setOverwrite(true);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptySourceList() {
        UnpackParameters victim = new UnpackParameters(output);
        TestUtils.assertInvalidParameters(victim);
    }
}
