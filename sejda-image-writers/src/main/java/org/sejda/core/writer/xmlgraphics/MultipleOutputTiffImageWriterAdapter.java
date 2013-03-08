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
