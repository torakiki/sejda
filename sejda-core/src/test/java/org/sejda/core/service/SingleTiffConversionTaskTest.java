/*
 * Created on 16/set/2011
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
package org.sejda.core.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlgraphics.image.loader.ImageException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.ImageTestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileOutput;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SingleTiffConversionTaskTest implements TestableTask<PdfToSingleTiffParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private AbstractPdfToSingleImageParameters getSingleTiffParams() {
        PdfToSingleTiffParameters parameters = new PdfToSingleTiffParameters(ImageColorType.GRAY_SCALE);
        parameters.setCompressionType(TiffCompressionType.PACKBITS);
        setCommonParams(parameters);
        return parameters;
    }

    private void setCommonParams(AbstractPdfToImageParameters parameters) {
        parameters.setResolutionInDpi(96);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_test_test_file.pdf", "test");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStreamToSingleTiff() throws TaskException, IOException, ImageException {
        AbstractPdfToSingleImageParameters parameters = getSingleTiffParams();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        File out = File.createTempFile("SejdaTest", ".tiff");
        out.deleteOnExit();
        parameters.setOutput(FileOutput.newInstance(out));
        victim.execute(parameters);
        RenderedImage ri = ImageTestUtils.loadImage(out);
        assertTrue(ri.getHeight() > 0);
        assertTrue(ri.getWidth() > 0);
    }

}
