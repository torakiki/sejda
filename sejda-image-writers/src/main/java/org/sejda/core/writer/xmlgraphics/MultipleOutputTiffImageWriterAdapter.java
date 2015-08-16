/*
 * Created on 19/set/2011
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

import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.internal.TIFFImageWriter;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;

/**
 * Adapts the xmlgraphics Tiff writer implementation to the Sejda {@link org.sejda.core.writer.model.ImageWriter} interface. This writer is NOT capable of writing multiple images
 * into a single output image.
 * 
 * @author Andrea Vacondio
 * 
 */
// PMD reports a false positive on this class (https://sourceforge.net/tracker/?func=detail&aid=3110548&group_id=56262&atid=479921)
final class MultipleOutputTiffImageWriterAdapter extends BaseTiffImageWriterAdapter<PdfToMultipleTiffParameters> {

    private org.apache.xmlgraphics.image.writer.ImageWriter adaptedWriter;

    private MultipleOutputTiffImageWriterAdapter() {
        adaptedWriter = new TIFFImageWriter();
    }

    public void openWriteDestination(OutputStream destination, PdfToMultipleTiffParameters params) {
        setOutputStream(destination);
    }

    public void write(RenderedImage image, PdfToMultipleTiffParameters params) throws TaskIOException {
        if (getOutputDestination() == null) {
            throw new TaskIOException("Cannot call write before opening the write destination");
        }
        ImageWriterParams imageWriterParams = newImageWriterParams(params, params.getCompressionType());
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
     * Builder for the {@link MultipleOutputTiffImageWriterAdapter}.
     * 
     * @author Andrea Vacondio
     * 
     */
    static final class MultipleOutputTiffImageWriterAdapterBuilder implements
            ImageWriterBuilder<PdfToMultipleTiffParameters> {

        public MultipleOutputTiffImageWriterAdapter build() {
            return new MultipleOutputTiffImageWriterAdapter();
        }
    }
}
