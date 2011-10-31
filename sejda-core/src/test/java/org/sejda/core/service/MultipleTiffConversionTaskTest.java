/*
 * Created on 26/set/2011
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.ImageTestUtils;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.StreamOutput;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class MultipleTiffConversionTaskTest implements TestableTask<PdfToMultipleTiffParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private AbstractPdfToMultipleImageParameters getMultipleTiffParams() {
        PdfToMultipleTiffParameters parameters = new PdfToMultipleTiffParameters(ImageColorType.GRAY_SCALE);
        parameters.setCompressionType(TiffCompressionType.PACKBITS);
        parameters.setOutputPrefix("[CURRENTPAGE]");
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
    public void testExecuteStreamToMultipleTiff() throws TaskException, IOException {
        AbstractPdfToMultipleImageParameters parameters = getMultipleTiffParams();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parameters.setOutput(StreamOutput.newInstance(out));
        victim.execute(parameters);
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            RenderedImage ri = ImageTestUtils.loadImage(zip, entry.getName());
            assertTrue(ri.getHeight() > 0);
            assertTrue(ri.getWidth() > 0);
            zip.closeEntry();
            entry = zip.getNextEntry();
        }

    }
}
