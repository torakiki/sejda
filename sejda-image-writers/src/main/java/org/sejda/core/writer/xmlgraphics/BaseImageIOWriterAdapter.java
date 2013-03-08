/*
 * Created on 08/mar/2013
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
package org.sejda.core.writer.xmlgraphics;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;

/**
 * Base ImageWriterAdapter wrapping an ImageIO {@link ImageWriter}
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            task parameter
 */
abstract class BaseImageIOWriterAdapter<T extends AbstractPdfToImageParameters> extends AbstractImageWriterAdapter<T> {

    private ImageWriter adapted = null;

    BaseImageIOWriterAdapter(String imageFormat) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(imageFormat);
        if (writers == null || !writers.hasNext()) {
            throw new IllegalArgumentException(String.format("Unable to find an ImageWriter for the format %s",
                    imageFormat));
        }
        // we use the first
        adapted = writers.next();
    }

    public void openWriteDestination(OutputStream destination, T params) {
        setOutputStream(destination);
    }

    public void write(RenderedImage image, T params) throws TaskIOException {
        if (adapted == null) {
            throw new TaskIOException("No ImageWriter available");
        }
        if (getOutputDestination() == null) {
            throw new TaskIOException("Cannot call write before opening the write destination");
        }
        ImageWriteParam imageWriterParams = newImageWriterParams(params);
        ImageOutputStream imageOut = null;
        try {
            imageOut = ImageIO.createImageOutputStream(getOutputDestination());
            adapted.setOutput(imageOut);
            adapted.write(null, new IIOImage(image, null, null), imageWriterParams);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }
    }

    /**
     * @param params
     * @return parameters for the {@link ImageWriter}. Each concrete class has to implement this to provide specific parameters for the adapted writer.
     */
    abstract ImageWriteParam newImageWriterParams(T params);

    ImageWriter getAdapted() {
        return adapted;
    }

    public boolean supportMultiImage() {
        return false;
    }

    @Override
    public void close() throws IOException {
        if (adapted != null) {
            adapted.dispose();
        }
        adapted = null;
        super.close();
    }

}
