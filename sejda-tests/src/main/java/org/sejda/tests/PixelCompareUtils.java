/*
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.tests;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.tests.TestUtils.customInput;

public class PixelCompareUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PixelCompareUtils.class);
    
    private double percentSimilarityThreshold;
    
    public PixelCompareUtils(){
        this(99.99);
    }
    public PixelCompareUtils(double percentSimilarityThreshold) {
        this.percentSimilarityThreshold = percentSimilarityThreshold;
    }

    public static void assertIsSimilar(PDDocument actual, String doc2ResourcePath) {
        new PixelCompareUtils().assertSimilar(actual, doc2ResourcePath);
    }
    
    public void assertSimilar(PDDocument actual, String doc2ResourcePath) {
        try {
            PDDocument expected;
            if(new File(doc2ResourcePath).exists()) {
                expected = PDFParser.parse(SeekableSources.seekableSourceFrom(new File(doc2ResourcePath)));
            } else {
                expected = PDFParser.parse(SeekableSources.onTempFileSeekableSourceFrom(
                        customInput(doc2ResourcePath).getSource()));
            }
            
            assertSimilar(actual, expected, Integer.MAX_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void assertSimilar(PDDocument actual, PDDocument expected) {
        assertSimilar(actual, expected, Integer.MAX_VALUE);
    }

    public void assertSimilar(PDDocument actual, PDDocument expected, int maxNumberOfPages) {
        try {
            if(actual.getNumberOfPages() != expected.getNumberOfPages()) {
                fail("Documents have different number of pages:" + actual.getNumberOfPages() + ", " + expected.getNumberOfPages());
            }

            int numOfPages = Math.min(actual.getNumberOfPages(), maxNumberOfPages);

            for (int i = 0; i < numOfPages; i++) {
                LOG.info("Comparing page " + (i + 1));

                BufferedImage p1 = resizeTo(takeScreenshotOf(actual, i), 1024);
                BufferedImage p2 = resizeTo(takeScreenshotOf(expected, i), 1024);
                ImageComparisonResult comparisonResult = new ImageComparison(p1, p2).compareImages();
                if(ImageComparisonState.MATCH != comparisonResult.getImageComparisonState()) {
                    double percentSimilarity = 100 - comparisonResult.getDifferencePercent();
                    if(percentSimilarity < this.percentSimilarityThreshold) {

                        // we want a stable output file across test runs
                        // so when the test is run again it produces the same files
                        String testName = getJunitTestName();

                        File baseFolder = new File(SystemUtils.getJavaIoTmpDir(), "sejda-similarity-tests");
                        File folder = new File(baseFolder, testName);
                        folder.mkdirs();

                        File f1 = new File(folder, "p" + (i + 1) + "_actual.png");
                        File f2 = new File(folder, "p" + (i + 1) + "_expected.png");
                        ImageIO.write(p1, "png", f1);
                        ImageIO.write(p2, "png", f2);

                        File f3 = new File(folder, "p" + (i + 1) + "_comparison.png");
                        ImageIO.write(comparisonResult.getResult(), "png", f3);

                        LOG.error("Troubleshoot:\n" + f1 + "\n" + f2 + "\n\n" + f3);

                        fail("Page " + (i + 1) + " differs, similarity: " + percentSimilarity + "%");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String getJunitTestName() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = trace.length - 1; i > 0; --i) {
            StackTraceElement ste = trace[i];
            try {
                Class<?> cls = Class.forName(ste.getClassName());
                Method method = cls.getDeclaredMethod(ste.getMethodName());
                Test annotation = method.getAnnotation(Test.class);
                if (annotation != null) {
                    return ste.getClassName() + "." + ste.getMethodName();
                }
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e) {
            }
        }
        
        return "unknownTest" + new Date().getTime();
    }

    public static BufferedImage takeScreenshotOf(PDDocument doc, int pageIndex) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        return pdfRenderer.renderImageWithDPI(pageIndex, 150);
    }

    private static BufferedImage resizeTo(BufferedImage image, int width) throws IOException {
        return Thumbnails.of(image).scalingMode(ScalingMode.PROGRESSIVE_BILINEAR).width(width).asBufferedImage();
    }
}
