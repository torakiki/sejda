/*
 * Created on 02 feb 2017
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.writer.imageio;

import static java.util.Objects.nonNull;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToImageParameters;

/**
 * Abstract implementation of an image writer that supports writing multiple images into a single file using ImageIO
 * 
 * @author Andrea Vacondio
 */
abstract class MultiImageWriter<T extends PdfToImageParameters> extends AbstractImageWriter<T> {

    private boolean prepared = false;
    private ImageWriteParam imageWriterParams;

    MultiImageWriter(String format) {
        super(format);
        if (!writer.canWriteSequence()) {
            throw new UnsupportedOperationException(
                    "The ImageWriter does not support writing multiple images to a single image file.");
        }
    }

    @Override
    public void openDestination(File file, T params) throws TaskIOException {
        super.openDestination(file, params);
        imageWriterParams = newImageWriterParams(params);
    }

    @Override
    public void write(RenderedImage image, T params) throws TaskIOException {
        TaskIOException.require(nonNull(getOutput()), "Cannot call write before opening the write destination");
        try {
            if (!prepared) {
                writer.prepareWriteSequence(null);
                prepared = true;
            }
            writer.writeToSequence(new IIOImage(image, null, newImageMetadata(image, params, imageWriterParams)),
                    imageWriterParams);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }
    }

    @Override
    public void closeDestination() throws TaskIOException {
        if (nonNull(writer)) {
            try {
                writer.endWriteSequence();
            } catch (IOException e) {
                throw new TaskIOException("An error occurred while ending write sequence", e);
            }
        }
        super.closeDestination();
    }

    @Override
    public boolean supportMultiImage() {
        return true;
    }
}
