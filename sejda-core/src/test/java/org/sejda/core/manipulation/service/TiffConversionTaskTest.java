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
package org.sejda.core.manipulation.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.image.ImageColorType;
import org.sejda.core.manipulation.model.image.TiffCompressionType;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.output.FileOutput;
import org.sejda.core.manipulation.model.parameter.image.PdfToSingleTiffParameters;
import org.sejda.core.manipulation.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class TiffConversionTaskTest implements TestableTask<PdfToSingleTiffParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private PdfToSingleTiffParameters parameters;

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the unpack parameters
     * 
     */
    private void setUpParameters() {
        parameters = new PdfToSingleTiffParameters(ImageColorType.GRAY_SCALE);
        parameters.setResolutionInDpi(96);
        parameters.setCompressionType(TiffCompressionType.PACKBITS);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException, ImageException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        File out = File.createTempFile("SejdaTest", ".tiff");
        out.deleteOnExit();
        parameters.setOutput(FileOutput.newInstance(out));
        victim.execute(parameters);
        RenderedImage ri = loadImage(out);
        assertTrue(ri.getHeight() > 0);
        assertTrue(ri.getWidth() > 0);
    }

    private RenderedImage loadImage(File image) throws ImageException, IOException {
        ImageManager imageManager = new ImageManager(new DefaultImageContext());
        ImageSessionContext sessionContext = new DefaultImageSessionContext(imageManager.getImageContext(), null);

        ImageInfo info = imageManager.getImageInfo(image.toURI().toString(), sessionContext);
        Image img = imageManager.getImage(info, ImageFlavor.RENDERED_IMAGE, sessionContext);

        ImageRendered imageRend = (ImageRendered) img;
        return imageRend.getRenderedImage();

    }
}
