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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.core.manipulation.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Test verifying that the --compressed and --pdfVersion flags can be specified for each task creating pdf outputs
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfFormatOutputTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public final static Collection<Object[]> testParameters() {
        return TestableTask.allTasksExceptFor(TestableTask.UNPACK, TestableTask.EXTRACT_TEXT,
                TestableTask.PDF_TO_SINGLE_TIFF, TestableTask.PDF_TO_MULTIPLE_TIFF);
    }

    public PdfFormatOutputTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void onValueCompressed() {
        AbstractPdfOutputParameters result = defaultCommandLine().with("--compressed").invokeSejdaConsole();

        assertTrue(describeExpectations(), result.isCompressXref());
    }

    @Test
    public void offValueCompressed() {
        AbstractPdfOutputParameters result = defaultCommandLine().invokeSejdaConsole();

        assertFalse(describeExpectations(), result.isCompressXref());
    }

    @Test
    public void specifiedValuePdfVersion() {
        AbstractPdfOutputParameters result = defaultCommandLine().with("--pdfVersion", "4").invokeSejdaConsole();

        assertEquals(describeExpectations(), PdfVersion.VERSION_1_4, result.getVersion());
    }

    @Test
    public void defaultValuePdfVersion() {
        AbstractPdfOutputParameters result = defaultCommandLine().invokeSejdaConsole();

        assertEquals(describeExpectations(), PdfVersion.VERSION_1_6, result.getVersion());
    }
}
