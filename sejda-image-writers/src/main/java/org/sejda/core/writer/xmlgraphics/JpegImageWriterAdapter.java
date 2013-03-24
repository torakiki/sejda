/*
 * Created on 01/mar/2013
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

import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.imageio.ImageIOJPEGImageWriter;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToJpegParameters;

import javax.imageio.ImageWriteParam;

/**
 * Adapts the xmlgraphics JPEG writer implementation to the Sejda {@link org.sejda.core.writer.model.ImageWriter} interface. This writer is NOT capable of writing multiple images
 * into a single output image.
 * 
 * @author Andrea Vacondio
 * 
 */
final class JpegImageWriterAdapter extends AbstractImageWriterAdapter<PdfToJpegParameters> {

    private ImageWriter adaptedWriter;

    private JpegImageWriterAdapter() {
        adaptedWriter = new ImageIOJPEGImageWriter()
        {

            @Override
            protected ImageWriteParam getDefaultWriteParam(
                    javax.imageio.ImageWriter iiowriter, RenderedImage image,
                    ImageWriterParams params) {
                ImageWriteParam param = super.getDefaultWriteParam(iiowriter, image, params);
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(1.0f);
                return param;
            }
        }
        ;
    }

    public void openWriteDestination(OutputStream destination, PdfToJpegParameters params) {
        setOutputStream(destination);
    }

    public void write(RenderedImage image, PdfToJpegParameters params) throws TaskIOException {
        if (getOutputDestination() == null) {
            throw new TaskIOException("Cannot call write before opening the write destination");
        }
        ImageWriterParams imageWriterParams = newImageWriterParams(params);
        try {
            adaptedWriter.writeImage(image, getOutputDestination(), imageWriterParams);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }

    }

    public boolean supportMultiImage() {
        return false;
    }

    @Override
    public void close() throws IOException {
        adaptedWriter = null;
        super.close();
    }

    /**
     * 
     * @param params
     * @param compressionType
     * @return a new {@link ImageWriterParams} for the given input.
     */
    private ImageWriterParams newImageWriterParams(PdfToJpegParameters params) {
        ImageWriterParams imageWriterParams = new ImageWriterParams();
        imageWriterParams.setResolution(params.getResolutionInDpi());
        return imageWriterParams;
    }

    /**
     * Builder for the {@link JpegImageWriterAdapter}.
     * 
     * @author Andrea Vacondio
     * 
     */
    static final class JpegImageWriterAdapterBuilder implements ImageWriterBuilder<PdfToJpegParameters> {

        public JpegImageWriterAdapter build() {
            return new JpegImageWriterAdapter();
        }
    }
}
