/*
 * Created on 05 dic 2016
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
package org.sejda.impl.sambox.ocr.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.sejda.io.FastByteArrayOutputStream;

/**
 * @author Andrea Vacondio
 *
 */
public class ImageUtils {

    public static ByteBuffer convertToTiff(BufferedImage image) throws IOException {
        FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
        try (ImageOutputStream output = ImageIO.createImageOutputStream(bos)) {
            ImageIO.write(image, "tiff", output);
        }
        return convertImageData(ImageIO.read(new ByteArrayInputStream(bos.toByteArray())));
    }

    /**
     * Converts <code>BufferedImage</code> to <code>ByteBuffer</code>.
     *
     * @param bi
     *            Input image
     * @return pixel data
     */
    public static ByteBuffer convertImageData(BufferedImage bi) {
        DataBuffer buff = bi.getRaster().getDataBuffer();
        // ClassCastException thrown if buff not instanceof DataBufferByte because raster data is not necessarily bytes.
        // Convert the original buffered image to grayscale.
        if (!(buff instanceof DataBufferByte)) {
            bi = convertImageToGrayscale(bi);
            buff = bi.getRaster().getDataBuffer();
        }
        byte[] pixelData = ((DataBufferByte) buff).getData();
        // return ByteBuffer.wrap(pixelData);
        ByteBuffer buf = ByteBuffer.allocateDirect(pixelData.length);
        buf.order(ByteOrder.nativeOrder());
        buf.put(pixelData);
        buf.flip();
        return buf;
    }

    public static BufferedImage convertImageToGrayscale(BufferedImage image) {
        BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = tmp.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return tmp;
    }

}
