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
import org.sejda.sambox.contentstream.operator.DrawObject;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.contentstream.operator.state.Concatenate;
import org.sejda.sambox.contentstream.operator.state.Restore;
import org.sejda.sambox.contentstream.operator.state.Save;
import org.sejda.sambox.contentstream.operator.state.SetGraphicsStateParameters;
import org.sejda.sambox.contentstream.operator.state.SetMatrix;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.encryption.MessageDigests;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.util.Matrix;
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
    private Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> optimizedById = new HashMap<>();
    private OptimizeParameters parameters;

    ImagesOptimizer(OptimizeParameters parameters) {
        this.parameters = parameters;
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
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
                        context.getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT, COSDictionary.class))
                                .map(d -> d.getDictionaryObject(objectName)).orElseThrow(
                                        () -> new MissingResourceException("Missing XObject: " + objectName.getName()));

                if (!(existing instanceof ReadOnlyFilteredCOSStream)) {
                    COSStream stream = (COSStream)existing;
                    String subtype = stream.getNameAsString(COSName.SUBTYPE);
                    if (COSName.IMAGE.getName().equals(subtype)) {
                        long unfilteredSize = stream.getFilteredLength();

                        removeMetadataIfNeeded(stream);
                        removeAlternatesIfNeeded(stream);

                        if (parameters.getOptimizations().contains(Optimization.COMPRESS_IMAGES)) {
                            boolean jbig2Image = isJbig2Image(stream);
                            if(jbig2Image) {
                                LOG.debug("Skipping JBIG2 encoded image");
                            }

                            if (unfilteredSize > parameters.getImageMinBytesSize() && !jbig2Image) {
                                long start = System.currentTimeMillis();
                                PDXObject xobject = PDXObject.createXObject(stream.getCOSObject(),
                                        context.getResources());
                                long elapsed = System.currentTimeMillis() - start;
                                if(elapsed > 500) LOG.debug("Loading PDXObject took " + elapsed + "ms");

                                PDImageXObject image = (PDImageXObject) xobject;

                                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                                float imageXScale = ctmNew.getScalingFactorX();
                                float imageYScale = ctmNew.getScalingFactorY();

                                int displayHeight = (int)(imageYScale / 72.0f * parameters.getImageDpi());
                                int displayWidth = (int)(imageXScale / 72.0f * parameters.getImageDpi());

                                LOG.debug("Found image {}x{} (displayed as {}x{}, scaled as {}x{}) with size {}",
                                        image.getHeight(), image.getWidth(), displayHeight, displayWidth, imageYScale, imageXScale, unfilteredSize);

                                optimize(objectName, image, stream.id(), displayWidth, displayHeight);
                            }
                        }
                    } else if (COSName.FORM.getName().equals(subtype)) {
                        PDXObject xobject = PDXObject.createXObject(existing.getCOSObject(), context.getResources());
                        removeMetadataIfNeeded(xobject.getCOSObject());
                        removePieceInfoIfNeeded(xobject.getCOSObject());
                        showForm((PDFormXObject) xobject);
                    }
                }
            }
        }

        private boolean isJbig2Image(COSStream image) {
            COSBase filters = image.getFilters();
            if (filters instanceof COSName) {
                return COSName.JBIG2_DECODE.equals(filters);
            }
            if (filters instanceof COSArray) {
                return ((COSArray) filters).contains(COSName.JBIG2_DECODE);
            }
            return false;
        }

        private void optimize(COSName objectName, PDImageXObject image, IndirectCOSObjectIdentifier id, int displayWidth, int displayHeight) {
            try {
                LOG.debug("Optimizing image {} {} with dimensions {}x{}", objectName.getName(), id.toString(), image.getImage().getWidth(), image.getImage().getHeight());
                ReadOnlyFilteredCOSStream optimizedImage = optimizedById.get(id);

                if(optimizedImage == null) {
                    long start = System.currentTimeMillis();
                    File tmpImageFile = ImageOptimizer.optimize(image.getImage(), parameters.getImageQuality(),
                            parameters.getImageDpi(), displayWidth, displayHeight);

                    long elapsed = System.currentTimeMillis() - start;
                    if(elapsed > 500) LOG.debug("Optimizing image took " + elapsed + "ms");

                    // we wrap the existing so we can identify it later as "in use" and already processed
                    optimizedImage = ReadOnlyFilteredCOSStream.readOnly(image.getCOSObject());

                    double sizeRate = tmpImageFile.length() * 100.0 / image.getCOSObject().getFilteredLength();
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
                            optimizedById.put(id, optimizedImage);
                        } else {
                            LOG.debug("Reusing previously optimized image");
                        }
                    } else {
                        LOG.debug(String.format("Skipping already compressed image, result is %.2f%% of original size",
                                sizeRate));
                    }
                } else {
                    LOG.debug(String.format("Skipping already compressed image with id %s", id));
                }

                COSDictionary resources = context.getResources().getCOSObject();
                COSDictionary xobjects = ofNullable(resources.getDictionaryObject(COSName.XOBJECT))
                        .filter(b -> b instanceof COSDictionary).map(b -> (COSDictionary) b).orElseGet(() -> {
                            COSDictionary ret = new COSDictionary();
                            resources.setItem(COSName.XOBJECT, ret);
                            return ret;
                        });
                xobjects.setItem(objectName, optimizedImage);
                // free up resources used by the underlying COSStream
                // which stores both the filtered and unfiltered bytes[] and DecodeResult
                // potentially creating a large memory footprint
                image.getCOSObject().unDecode();

            } catch (IOException | RuntimeException ex) {
                LOG.warn("Failed to optimize image, skipping and continuing with next.", ex);
            }
        }

        private void removeMetadataIfNeeded(COSStream cosObject) {
            if (parameters.getOptimizations().contains(Optimization.DISCARD_METADATA)) {
                cosObject.removeItem(COSName.METADATA);
            }
        }

        private void removePieceInfoIfNeeded(COSStream cosObject) {
            if (parameters.getOptimizations().contains(Optimization.DISCARD_PIECE_INFO)) {
                cosObject.removeItem(COSName.PIECE_INFO);
            }
        }

        private void removeAlternatesIfNeeded(COSStream cosObject) {
            if (parameters.getOptimizations().contains(Optimization.DISCARD_ALTERNATE_IMAGES)) {
                cosObject.removeItem(COSName.getPDFName("Alternates"));
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
        return ReadOnlyFilteredCOSStream.readOnlyJpegImage(file, awtImage.getWidth(), awtImage.getHeight(),
                awtImage.getColorModel().getComponentSize(0), getColorSpaceFromAWT(awtImage));
    }

    public static boolean canOptimizeFor(Optimization o) {
        return o == Optimization.COMPRESS_IMAGES || o == Optimization.DISCARD_ALTERNATE_IMAGES
                || o == Optimization.DISCARD_PIECE_INFO || o == Optimization.DISCARD_METADATA;
    }
}
