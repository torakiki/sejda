/*
 * Copyright 2017 by Eduard Weissmann
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
package org.sejda.core.writer.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class ImageOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(ImageOptimizer.class);

    /**
     * Takes an image and creates an optimized version of it.
     *
     * If the image is larger than maxWidthOrHeight pixels, it is downsized to fit the maxWidthOrHeight rectangle (keeping its aspect ratio). Image is saved as JPEG with specified
     * quality (1.0 is best/leave unchanged, 0.0 is worst). Image DPI is changed to dpi specified.
     */
    public static File optimize(BufferedImage bufferedImage, float quality, int dpi, int width, int height,
            boolean gray) throws IOException {
        long start = System.currentTimeMillis();
        File outputFile = File.createTempFile("pdfimage", ".jpeg");
        outputFile.deleteOnExit();

        try {
            int relevantDelta = 20;
            boolean isResizeRelevant = Math.abs(bufferedImage.getWidth() - width) > relevantDelta
                    && Math.abs(bufferedImage.getHeight() - height) > relevantDelta;
            boolean isShirinking = bufferedImage.getHeight() > height || bufferedImage.getWidth() > width;

            if (isResizeRelevant && isShirinking) {
                // we resize down, we don't resize up
                LOG.debug("Resizing image from {}x{} to {}x{}", bufferedImage.getWidth(), bufferedImage.getHeight(),
                        width, height);
                bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.BALANCED, width, height);
            }

            // PNG read fix when converting to JPEG
            BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    gray ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            g2d.dispose();

            ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
            imageWriter.setOutput(ios);

            // compression
            JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(quality);

            IIOMetadata imageMetaData = null;
            try {
                // new metadata
                imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(newImage), jpegParams);
                Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
                Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("Xdensity", Integer.toString(dpi));
                jfif.setAttribute("Ydensity", Integer.toString(dpi));
                jfif.setAttribute("resUnits", "1");
                imageMetaData.setFromTree("javax_imageio_jpeg_image_1.0", tree);
            } catch (Exception e) {
                LOG.warn("Failed to set DPI for image, metadata manipulation failed", e);
            }

            try {
                imageWriter.write(null, new IIOImage(newImage, null, imageMetaData), jpegParams);
            } finally {
                IOUtils.closeQuietly(ios);
                imageWriter.dispose();
            }

            return outputFile;
        } finally {
            bufferedImage.flush();
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > 500)
                LOG.trace("Optimizing image took " + elapsed + "ms");
        }
    }
}
