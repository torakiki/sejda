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
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.core.manipulation.model.image.TiffCompressionType;
import org.sejda.core.manipulation.model.parameter.image.PdfToTiffParameters;

/**
 * Tests for tasks having pdf as input and tiff image format as output
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfToTiffTraitTest extends AbstractTaskTraitTest {

    public PdfToTiffTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    private static final TiffCompressionType DEFAULT_COMPRESSION_TYPE = TiffCompressionType.NONE;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { TestableTask.PDF_TO_MULTIPLE_TIFF },
                { TestableTask.PDF_TO_SINGLE_TIFF } });
    }

    @Test
    public void compressionType_default() {
        PdfToTiffParameters result = defaultCommandLine().without("--compressionType").invokeSejdaConsole();
        assertThat(result.getCompressionType(), is(DEFAULT_COMPRESSION_TYPE));
    }

    @Test
    public void compressionType() {
        PdfToTiffParameters result = defaultCommandLine().with("--compressionType", "jpeg_ttn2").invokeSejdaConsole();
        assertThat(result.getCompressionType(), is(TiffCompressionType.JPEG_TTN2));
    }

}
