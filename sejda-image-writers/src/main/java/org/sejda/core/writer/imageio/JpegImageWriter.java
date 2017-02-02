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

import javax.imageio.ImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * JPEG image writer using ImageIO
 * 
 * @author Andrea Vacondio
 */
public class JpegImageWriter extends SingleImageWriter<PdfToJpegParameters> {

    public JpegImageWriter() {
        super("jpeg");
    }

    @Override
    ImageWriteParam newImageWriterParams(PdfToJpegParameters params) {
        JPEGImageWriteParam param = new JPEGImageWriteParam(writer.getLocale());
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality((float) params.getQuality() / 100);
        return param;
    }
}
