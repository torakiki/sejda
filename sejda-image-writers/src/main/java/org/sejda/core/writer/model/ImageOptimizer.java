package org.sejda.core.writer.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;

public class ImageOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(ImageOptimizer.class);

    /**
     * Takes an image and creates an optimized version of it.
     *
     * If the image is larger than maxWidthOrHeight pixels, it is downsized to fit the maxWidthOrHeight rectangle (keeping its aspect ratio). Image is saved as JPEG with specified
     * quality (1.0 is best/leave unchanged, 0.0 is worst). Image DPI is changed to dpi specified.
     */
    public static File optimize(BufferedImage bufferedImage, float quality, int dpi, int maxWidthOrHeight)
            throws IOException {
        File outputFile = File.createTempFile("pdfimage", ".jpeg");
        outputFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(outputFile);

        try {
            if (bufferedImage.getHeight() > maxWidthOrHeight || bufferedImage.getWidth() > maxWidthOrHeight) {
                bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, maxWidthOrHeight);
            }

            // PNG read fix when converting to JPEG
            BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            imageRGB.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

            JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
            imageWriter.setOutput(ios);

            IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(imageRGB), null);

            try {
                // new metadata
                Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
                Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("Xdensity", Integer.toString(dpi));
                jfif.setAttribute("Ydensity", Integer.toString(dpi));
            } catch (Exception e) {
                LOG.warn("Failed to set DPI for image, metadata manipulation failed", e);
            }

            JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(quality);

            try {
                imageWriter.write(imageMetaData, new IIOImage(imageRGB, null, null), jpegParams);
            } finally {
                IOUtils.closeQuietly(ios);
                imageWriter.dispose();
            }

            return outputFile;
        } finally {
            IOUtils.closeQuietly(fos);
            bufferedImage.flush();
        }
    }
}
