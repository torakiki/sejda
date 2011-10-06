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

import org.junit.Test;
import org.sejda.core.manipulation.model.image.TiffCompressionType;
import org.sejda.core.manipulation.model.parameter.image.PdfToMultipleTiffParameters;

/**
 * Tests for PdfToMultipleTiff task
 * 
 * @author Eduard Weissmann
 * 
 */
// TODO: EW: this is pretty much duplicate or PdfToSingleTiffTaskTest. Problem is PdfToSingleTiffParameters and PdfToMultipleTiffParameters have no base class such as
// PdfToTiffParameters on which a generic test can be written. Revise and maybe find a better solution for the params model and tests
public class PdfToMultipleTiffTaskTest extends AbstractTaskTest {

    private static final TiffCompressionType DEFAULT_COMPRESSION_TYPE = TiffCompressionType.NONE;

    public PdfToMultipleTiffTaskTest() {
        super(TestableTask.PDF_TO_MULTIPLE_TIFF);
    }

    @Test
    public void compressionType_default() {
        PdfToMultipleTiffParameters result = defaultCommandLine().without("--compressionType").invokeSejdaConsole();
        assertThat(result.getCompressionType(), is(DEFAULT_COMPRESSION_TYPE));
    }

    @Test
    public void compressionType() {
        PdfToMultipleTiffParameters result = defaultCommandLine().with("--compressionType", "jpeg_ttn2")
                .invokeSejdaConsole();
        assertThat(result.getCompressionType(), is(TiffCompressionType.JPEG_TTN2));
    }

}
