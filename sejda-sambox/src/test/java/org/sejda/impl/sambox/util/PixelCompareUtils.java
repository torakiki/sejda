/*
 * Copyright 2021 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox.util;

import static org.sejda.core.service.BaseTaskTest.customInput;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.sejda.core.support.io.IOUtils;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;

public class PixelCompareUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PixelCompareUtils.class);

    public static void assertSimilar(PDDocumentHandler doc1, String doc2ResourcePath) {
        assertSimilar(doc1.getUnderlyingPDDocument(), doc2ResourcePath);
    }

    public static void assertSimilar(PDDocument doc1, String doc2ResourcePath) {
        try {
            PDDocument doc2 = customInput(doc2ResourcePath).open(new DefaultPdfSourceOpener())
                    .getUnderlyingPDDocument();
            assertSimilar(doc1, doc2, Integer.MAX_VALUE);
        } catch (TaskIOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertSimilar(PDDocument doc1, PDDocument doc2) {
        assertSimilar(doc1, doc2, Integer.MAX_VALUE);
    }

    public static void assertSimilar(PDDocument doc1, PDDocument doc2, int maxNumberOfPages) {
        try {
            if (doc1.getNumberOfPages() != doc2.getNumberOfPages()) {
                Assert.fail("Documents have different number of pages:" + doc1.getNumberOfPages() + ", "
                        + doc2.getNumberOfPages());
            }

            int numOfPages = Math.min(doc1.getNumberOfPages(), maxNumberOfPages);

            for (int i = 0; i < numOfPages; i++) {
                LOG.info("Comparing page " + (i + 1));

                BufferedImage p1 = takeScreenshotOf(doc1, i);
                BufferedImage p2 = takeScreenshotOf(doc2, i);
                double similary = pixelSimilarityOf(p1, p2);

                if (similary < 99.5d) {
                    File folder = IOUtils.createTemporaryFolder();
                    File f1 = new File(folder, "p" + (i + 1) + "_1.png");
                    File f2 = new File(folder, "p" + (i + 1) + "_2.png");
                    ImageIO.write(p1, "png", f1);
                    ImageIO.write(p2, "png", f2);
                    LOG.error("Troubleshoot:\n" + f1 + "\n" + f2);

                    Assert.fail("Page " + (i + 1) + " differs, similarity is: " + pixelSimilarityOf(p1, p2));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage takeScreenshotOf(PDDocument doc, int pageIndex) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        return pdfRenderer.renderImageWithDPI(pageIndex, 150);
    }

    private static BufferedImage resizeTo(BufferedImage image, int width) throws IOException {
        return Thumbnails.of(image).scalingMode(ScalingMode.PROGRESSIVE_BILINEAR).width(width)
                .asBufferedImage();
    }

    private static double pixelSimilarityOf(BufferedImage src1, BufferedImage src2) throws IOException {
        BufferedImage img1 = resizeTo(src1, 1024);
        BufferedImage img2 = resizeTo(src2, 1024);

        double difference = 0d;
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                int rgbA = img1.getRGB(x, y);
                int rgbB = img2.getRGB(x, y);
                int redA = (rgbA >> 16) & 0xff;
                int greenA = (rgbA >> 8) & 0xff;
                int blueA = (rgbA) & 0xff;
                int redB = (rgbB >> 16) & 0xff;
                int greenB = (rgbB >> 8) & 0xff;
                int blueB = (rgbB) & 0xff;
                difference += Math.abs(redA - redB);
                difference += Math.abs(greenA - greenB);
                difference += Math.abs(blueA - blueB);
            }
        }

        // Total number of red pixels = width * height
        // Total number of blue pixels = width * height
        // Total number of green pixels = width * height
        // So total number of pixels = width * height * 3
        double totalPixels = img1.getWidth() * img1.getHeight() * 3d;

        // Normalizing the value of different pixels
        // for accuracy(average pixels per color
        // component)
        double avgDifferentPixels = difference / totalPixels;

        // There are 255 values of pixels in total
        double percentage = (avgDifferentPixels / 255) * 100d;

        return 100 - percentage;
    }
}
