/*
 * Created on 01 feb 2017
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
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToImageParameters;

/**
 * Abstract implementation of an image writer that doesn't support writing multiple images into a single file using ImageIO
 * 
 * @author Andrea Vacondio
 *
 */
abstract class SingleImageWriter<T extends PdfToImageParameters> extends AbstractImageWriter<T> {

    SingleImageWriter(String imageFormat) {
        super(imageFormat);
    }

    @Override
    public void write(RenderedImage image, T params) throws TaskIOException {
        TaskIOException.require(nonNull(getOutput()), "Cannot call write before opening the write destination");
        ImageWriteParam imageWriterParams = newImageWriterParams(params);
        try {
            writer.write(null, new IIOImage(image, null, newImageMetadata(image, params, imageWriterParams)),
                    imageWriterParams);
        } catch (IOException e) {
            throw new TaskIOException(e);
        }
    }

    @Override
    public boolean supportMultiImage() {
        return false;
    }
}
