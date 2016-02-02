/*
 * Created on 28 gen 2016
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
package org.sejda.impl.sambox.component.optimizaton;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.sejda.sambox.pdmodel.graphics.image.JPEGFactory.getColorSpaceFromAWT;
import static org.sejda.sambox.pdmodel.graphics.image.JPEGFactory.readJpegFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.sejda.core.writer.model.ImageOptimizer;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.encryption.MessageDigests;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that parses the page content stream and its annotations appearance stream and performs some optimization, depending on the input {@link OptimizeParameters}. It tries
 * to identify equal image xobjects and reuse them.
 * 
 * @author Andrea Vacondio
 *
 */
class ImagesOptimizer extends PDFStreamEngine implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesOptimizer.class);

    private Map<String, ReadOnlyFilteredCOSStream> optimizedByHash = new HashMap<>();
    private OptimizeParameters parameters;

    ImagesOptimizer(OptimizeParameters parameters) {
        this.parameters = parameters;
        addOperator(new XObjectOperator());
    }

    private class XObjectOperator extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.size() < 1) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {

                COSName objectName = (COSName) operand;
                COSBase existing = ofNullable(
                        context.getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT))
                                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                                .map(d -> d.getDictionaryObject(objectName)).orElseThrow(
                                        () -> new MissingResourceException("Missing XObject: " + objectName.getName()));

                if (!(existing instanceof ReadOnlyFilteredCOSStream)) {
                    PDXObject xobject = PDXObject.createXObject(existing.getCOSObject(), context.getResources());
                    if (xobject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xobject;
                        LOG.trace("Found image {}x{}", image.getHeight(), image.getWidth());
                        removeMetadataIfNeeded(image);
                        removeAlternatesIfNeeded(image);
                        if (parameters.getOptimizations().contains(Optimization.COMPRESS_IMAGES)) {
                            optimize(objectName, image);
                        }
                    } else if (xobject instanceof PDFormXObject) {
                        removeMetadataIfNeeded(xobject);
                        removePieceInfoIfNeeded(xobject);
                        showForm((PDFormXObject) xobject);
                    }
                }
            }
        }

        private void optimize(COSName objectName, PDImageXObject image) {
            try {
                LOG.debug("Optimizing image {}", objectName.getName());
                File tmpImageFile = ImageOptimizer.optimize(image.getImage(), parameters.getImageQuality(),
                        parameters.getImageDpi(), parameters.getImageMaxWidthOrHeight());

                // we wrap the existing so we can identify it later as "in use" and already processed
                ReadOnlyFilteredCOSStream optimizedImage = ReadOnlyFilteredCOSStream.readOnly(image.getCOSStream());

                double sizeRate = tmpImageFile.length() * 100.0 / image.getCOSStream().getFilteredLength();
                // can be compressed
                if (sizeRate < 100) {
                    String hash = Base64.getEncoder()
                            .encodeToString(MessageDigests.md5().digest(Files.readAllBytes(tmpImageFile.toPath())));
                    optimizedImage = optimizedByHash.get(hash);
                    // is it the same as something we already compressed?
                    if (isNull(optimizedImage)) {
                        LOG.debug(String.format("Compressed image to %.2f%% of original size", sizeRate));
                        optimizedImage = createFromJpegFile(tmpImageFile);
                        optimizedByHash.put(hash, optimizedImage);
                    } else {
                        LOG.debug("Reusing previously optimized image");
                    }
                } else {
                    LOG.trace(String.format("Skipping already compressed image, result is %.2f%% of original size",
                            sizeRate));
                }
                COSDictionary resources = context.getResources().getCOSObject();
                COSDictionary xobjects = ofNullable(resources.getDictionaryObject(COSName.XOBJECT))
                        .filter(b -> b instanceof COSDictionary).map(b -> (COSDictionary) b).orElseGet(() -> {
                            COSDictionary ret = new COSDictionary();
                            resources.setItem(COSName.XOBJECT, ret);
                            return ret;
                        });
                xobjects.setItem(objectName, optimizedImage);

            } catch (IOException | RuntimeException ex) {
                LOG.warn("Failed to optimize image, skipping and continuing with next.", ex);
            }
        }

        private void removeMetadataIfNeeded(PDXObject image) {
            if (parameters.getOptimizations().contains(Optimization.DISCARD_METADATA)) {
                image.getCOSStream().removeItem(COSName.METADATA);
            }
        }

        private void removePieceInfoIfNeeded(PDXObject image) {
            if (parameters.getOptimizations().contains(Optimization.DISCARD_PIECE_INFO)) {
                image.getCOSStream().removeItem(COSName.getPDFName("PieceInfo"));
            }
        }

        private void removeAlternatesIfNeeded(PDXObject image) {
            if (parameters.getOptimizations().contains(Optimization.DISCARD_ALTERNATE_IMAGES)) {
                image.getCOSStream().removeItem(COSName.getPDFName("Alternates"));
            }
        }

        @Override
        public String getName() {
            return "Do";
        }
    }

    @Override
    public void accept(PDPage page) {
        try {
            this.processPage(page);
            for (PDAnnotation annotation : page.getAnnotations()) {
                // this forces annotation appearance stream to be parsed. It's a form xobject so...
                this.showAnnotation(annotation);
            }
        } catch (IOException e) {
            LOG.warn("Failed to optimize page, skipping and continuing with next.", e);
        }
    }

    public static ReadOnlyFilteredCOSStream createFromJpegFile(File file) throws IOException {
        // read image
        BufferedImage awtImage = readJpegFile(file);
        if (awtImage.getColorModel().hasAlpha()) {
            throw new UnsupportedOperationException("alpha channel not implemented");
        }
        return ReadOnlyFilteredCOSStream.readOnlyJpegImage(new FileInputStream(file), awtImage.getWidth(),
                awtImage.getHeight(), awtImage.getColorModel().getComponentSize(0), getColorSpaceFromAWT(awtImage));
    }

    public static boolean canOptimizeFor(Optimization o) {
        return o == Optimization.COMPRESS_IMAGES || o == Optimization.DISCARD_ALTERNATE_IMAGES
                || o == Optimization.DISCARD_PIECE_INFO || o == Optimization.DISCARD_METADATA;
    }
}
