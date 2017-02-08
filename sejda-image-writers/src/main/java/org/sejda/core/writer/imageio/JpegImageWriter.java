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

import java.awt.image.RenderedImage;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * JPEG image writer using ImageIO
 * 
 * @author Andrea Vacondio
 */
public class JpegImageWriter extends SingleImageWriter<PdfToJpegParameters> {
    private static final Logger LOG = LoggerFactory.getLogger(JpegImageWriter.class);

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

    @Override
    public IIOMetadata newImageMetadata(RenderedImage image, PdfToJpegParameters params, ImageWriteParam writerParams) {
        // new metadatad
        IIOMetadata imageMetaData = null;
        try {
            imageMetaData = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), writerParams);
            Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
            Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
            jfif.setAttribute("Xdensity", Integer.toString(params.getResolutionInDpi()));
            jfif.setAttribute("Ydensity", Integer.toString(params.getResolutionInDpi()));
            jfif.setAttribute("resUnits", "1");
            imageMetaData.setFromTree("javax_imageio_jpeg_image_1.0", tree);
        } catch (Exception e1) {
            LOG.warn("Failed to set DPI for image, metadata manipulation failed", e1);
        }
        return imageMetaData;
    }

}
