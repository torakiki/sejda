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

import static java.util.Optional.ofNullable;

import javax.imageio.ImageWriteParam;

import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;

/**
 * Tiff image writer using ImageIO
 * 
 * @author Andrea Vacondio
 */
public class TiffSingleImageWriter extends SingleImageWriter<PdfToMultipleTiffParameters> {

    public TiffSingleImageWriter() {
        super("tiff");
    }

    @Override
    ImageWriteParam newImageWriterParams(PdfToMultipleTiffParameters params) {
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(TIFF_COMPRESSION_TYPE_CACHE
                .get(ofNullable(params.getCompressionType()).orElse(TiffCompressionType.NONE)));
        return param;
    }
}
