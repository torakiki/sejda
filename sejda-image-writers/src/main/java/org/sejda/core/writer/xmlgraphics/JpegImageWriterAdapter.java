/*
 * Created on 01/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.writer.xmlgraphics;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageWriteParam;

import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.imageio.ImageIOJPEGImageWriter;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToJpegParameters;

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
        adaptedWriter = new ImageIOJPEGImageWriter() {

            @Override
            protected ImageWriteParam getDefaultWriteParam(javax.imageio.ImageWriter iiowriter, RenderedImage image,
                    ImageWriterParams params) {
                ImageWriteParam param = super.getDefaultWriteParam(iiowriter, image, params);
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(1.0f);
                return param;
            }
        };
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
