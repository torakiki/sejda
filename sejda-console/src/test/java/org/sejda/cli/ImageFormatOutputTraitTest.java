/*
 * Created on Oct 2, 2011
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;

/**
 * Test verifying that the --colorType and --resolution flags can be specified for each task creating image outputs
 * 
 * @author Eduard Weissmann
 * 
 */
public class ImageFormatOutputTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { TestableTask.PDF_TO_SINGLE_TIFF } });
    }

    public ImageFormatOutputTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void colorType() {
        AbstractPdfToImageParameters result = defaultCommandLine().with("--colorType", "BLACK_AND_WHITE")
                .invokeSejdaConsole();

        assertThat(describeExpectations(), result.getOutputImageColorType(), is(ImageColorType.BLACK_AND_WHITE));
    }

    @Test
    public void colorType_isMandatory() {
        defaultCommandLine().without("--colorType").assertConsoleOutputContains("Option is mandatory: --colorType");
    }

    @Test
    public void resolution() {
        AbstractPdfToImageParameters result = defaultCommandLine().with("--resolution", "150").invokeSejdaConsole();

        assertThat(describeExpectations(), result.getResolutionInDpi(), is(150));
    }

    @Test
    public void resolution_isOptional() {
        AbstractPdfToImageParameters result = defaultCommandLine().without("--resolution").invokeSejdaConsole();

        assertNotNull(describeExpectations(), result);
    }
}
