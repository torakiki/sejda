/*
 * Created on 19/set/2011
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
package org.sejda.core.writer.model;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.MultiImageWriter;
import org.apache.xmlgraphics.image.writer.internal.TIFFImageWriter;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.parameter.image.PdfToSingleTiffParameters;

/**
 * Adapts the xmlgraphics Tiff writer implementation to the Sejda {@link ImageWriter} interface. This writer is capable of writing multiple images into a single output image.
 * 
 * @author Andrea Vacondio
 * 
 */
class XmlGraphicsSingleOutputTiffImageWriterAdapter extends
        AbstractXmlGraphicsImageWriterAdapter<PdfToSingleTiffParameters> {

    private MultiImageWriter adaptedWriter = null;

    public void openWriteDestination(OutputStream destination, PdfToSingleTiffParameters params) throws TaskIOException {
        setOutputStream(destination);
        try {
            adaptedWriter = new TIFFImageWriter().createMultiImageWriter(destination);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }
    }

    public void write(RenderedImage image, PdfToSingleTiffParameters params) throws TaskIOException {
        ImageWriterParams imageWriterParams = new ImageWriterParams();
        imageWriterParams.setResolution(params.getResolutionInDpi());
        setCompressionOnInputArgument(params.getCompressionType(), imageWriterParams);
        try {
            adaptedWriter.writeImage(image, imageWriterParams);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }

    }

    public boolean supportMultiImage() {
        return true;
    }

    @Override
    public void closeDestination() throws TaskIOException {
        try {
            nullSafeCloseMultiImageWriter();
        } catch (IOException e) {
            throw new TaskIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        nullSafeCloseMultiImageWriter();
        adaptedWriter = null;
        super.close();
    }

    private void nullSafeCloseMultiImageWriter() throws IOException {
        if (adaptedWriter != null) {
            adaptedWriter.close();
        }
    }

    /**
     * Builder for the {@link XmlGraphicsSingleOutputTiffImageWriterAdapter}.
     * 
     * @author Andrea Vacondio
     * 
     */
    static final class XmlGraphicsSingleOutputTiffImageWriterAdapterBuilder implements
            ImageWriterBuilder<PdfToSingleTiffParameters> {

        public ImageWriter<PdfToSingleTiffParameters> build() {
            return new XmlGraphicsSingleOutputTiffImageWriterAdapter();
        }

    }
}
