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

import static java.util.Optional.ofNullable;

import javax.imageio.ImageWriteParam;

import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * TIFF image writer using ImageIO and writing multiple pages to a single file
 * 
 * @author Andrea Vacondio
 */
public class TiffMultiImageWriter extends MultiImageWriter<PdfToSingleTiffParameters> {

    public TiffMultiImageWriter() {
        super("tiff");
    }

    @Override
    ImageWriteParam newImageWriterParams(PdfToSingleTiffParameters params) {
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(TIFF_COMPRESSION_TYPE_CACHE
                .get(ofNullable(params.getCompressionType()).orElse(TiffCompressionType.NONE)));
        return param;
    }
}
