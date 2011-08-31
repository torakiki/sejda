/*
 * Created on Aug 29, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.AbstractParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Test verifying that the pdf version can be specified
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfVersionTraitTest extends AcrossAllTasksTraitTest {

    public PdfVersionTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void specifiedValue() {
        AbstractParameters result = defaultCommandLine().with("--pdfVersion", "VERSION_1_4")
                .invokeSejdaConsole();

        assertEquals(describeExpectations(), PdfVersion.VERSION_1_4, result.getVersion());
    }

    @Test
    public void defaultValue() {
        AbstractParameters result = defaultCommandLine().invokeSejdaConsole();

        assertEquals(describeExpectations(), PdfVersion.VERSION_1_6, result.getVersion());
    }
}
