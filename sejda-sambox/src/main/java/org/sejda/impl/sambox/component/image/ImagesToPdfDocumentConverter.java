/*
 * Copyright 2017 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox.component.image;

import org.apache.commons.io.FilenameUtils;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageImageWriter;
import org.sejda.model.PageOrientation;
import org.sejda.model.PageSize;
import org.sejda.model.encryption.EncryptionAtRestPolicy;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.ImageMergeInput;
import org.sejda.model.input.MergeInput;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.Source;
import org.sejda.model.parameter.BaseMergeParameters;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.model.util.IOUtils.createTemporaryBufferWithName;

public class ImagesToPdfDocumentConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesToPdfDocumentConverter.class);

    private PDDocumentHandler documentHandler;
    private PageImageWriter imageWriter;
    
    public ImagesToPdfDocumentConverter() {
        this.documentHandler = new PDDocumentHandler();
        documentHandler.setCreatorOnPDDocument();

        this.imageWriter = new PageImageWriter(documentHandler.getUnderlyingPDDocument());
    }

    public List<PDPage> addPages(Source<?> source) throws TaskException {
        return addPages(source, null, PageOrientation.AUTO, 0);    
    }
    
    public List<PDPage> addPages(Source<?> source, PDRectangle pageSize, PageOrientation pageOrientation, 
                                     float marginInches) throws TaskException {
        beforeImage(source);
        List<PDPage> results = new LinkedList<>();
        try {
            
            int numberOfImages = 1;
            if(supportsMultiPageImage(source)) {
                numberOfImages = getImagePageCount(source);
            }
                
            for(int imageNumber = 0; imageNumber < numberOfImages; imageNumber++) {

                PDImageXObject image = PageImageWriter.toPDXImageObject(source, imageNumber);
                PDRectangle mediaBox = pageSize;
                if (mediaBox == null) {
                    mediaBox = new PDRectangle(image.getWidth(), image.getHeight());
                }

                if (pageOrientation == PageOrientation.LANDSCAPE) {
                    mediaBox = new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth());
                } else if (pageOrientation == PageOrientation.AUTO) {
                    if (image.getWidth() > image.getHeight() && image.getWidth() > mediaBox.getWidth()) {
                        LOG.debug("Switching to landscape, image dimensions are {}x{}", image.getWidth(),
                                image.getHeight());
                        mediaBox = new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth());
                    }
                }

                PDPage page = documentHandler.addBlankPage(mediaBox);
                results.add(page);

                // full page (scaled down only)
                float width = image.getWidth();
                float height = image.getHeight();

                if (width > mediaBox.getWidth()) {
                    float targetWidth = mediaBox.getWidth();
                    LOG.debug("Scaling image down to fit by width {} vs {}", width, targetWidth);

                    float ratio = width / targetWidth;
                    width = targetWidth;
                    height = Math.round(height / ratio);
                }

                if (height > mediaBox.getHeight()) {
                    float targetHeight = mediaBox.getHeight();
                    LOG.debug("Scaling image down to fit by height {} vs {}", height, targetHeight);

                    float ratio = height / targetHeight;
                    height = targetHeight;
                    width = Math.round(width / ratio);
                }

                if (marginInches > 0) {
                    float newWidth = width - marginInches * 72;
                    float newHeight = height * newWidth / width;
                    width = newWidth;
                    height = newHeight;
                }

                // centered on page
                float x = (mediaBox.getWidth() - width) / 2;
                float y = (mediaBox.getHeight() - height) / 2;

                imageWriter.append(page, image, new Point2D.Float(x, y), width, height, null, 0);

                afterImage(image);
            }
        } catch (TaskIOException e) {
            failedImage(source, e);
        }
        
        return results;
    }

    public void beforeImage(Source<?> source) throws TaskException {

    }

    public void afterImage(PDImageXObject image) throws TaskException {

    }

    public void failedImage(Source<?> source, TaskIOException e) throws TaskException {
        throw e;
    }
    
    public boolean supportsMultiPageImage(Source<?> source) {
        return true;
    }

    public static void convertImageMergeInputToPdf(BaseMergeParameters<MergeInput> parameters,
            TaskExecutionContext context) throws TaskException {
        // if images were supplied, convert them to PDF
        List<MergeInput> newInputList = new ArrayList<>();
        for (MergeInput input : parameters.getInputList()) {
            if (input instanceof ImageMergeInput image) {
                // collect all consecutive images and convert them to a PDF document
                context.notifiableTaskMetadata().setCurrentSource(image.getSource());
                newInputList.add(convertImagesToPdfMergeInput(image, context));
                context.notifiableTaskMetadata().clearCurrentSource();
            } else {
                newInputList.add(input);
            }
        }

        parameters.setInputList(newInputList);
    }
    
    public static PDRectangle toPDRectangle(PageSize pageSize) {
        return new PDRectangle(pageSize.getWidth(), pageSize.getHeight());
    }

    private static PdfMergeInput convertImagesToPdfMergeInput(ImageMergeInput image, TaskExecutionContext context)
            throws TaskException {
        ImagesToPdfDocumentConverter converter = new ImagesToPdfDocumentConverter() {
            @Override
            public void failedImage(Source<?> source, TaskIOException e) throws TaskException {
                context.assertTaskIsLenient(e);
                notifyEvent(context.notifiableTaskMetadata()).taskWarning(
                        String.format("Image %s was skipped, could not be processed", source.getName()), e);
            }
        };

        PDRectangle pageSize = null;
        if (image.getPageSize() != null) {
            pageSize = toPDRectangle(image.getPageSize());    
        }
        if (image.isShouldPageSizeMatchImageSize()) {
            pageSize = null;
        }

        converter.addPages(image.getSource(), pageSize, image.getPageOrientation(), 0);
        PDDocumentHandler converted = converter.getDocumentHandler();
        String basename = FilenameUtils.getBaseName(image.getSource().getName());
        String filename = String.format("%s.pdf", basename);
        File convertedTmpFile = createTemporaryBufferWithName(filename);
        converted.setDocumentTitle(basename);

        EncryptionAtRestPolicy encryptionAtRestPolicy = image.getSource().getEncryptionAtRestPolicy();
        converted.savePDDocument(convertedTmpFile, encryptionAtRestPolicy);

        PdfMergeInput input = new PdfMergeInput(PdfFileSource.newInstanceNoPassword(convertedTmpFile));
        input.getSource().setEncryptionAtRestPolicy(encryptionAtRestPolicy);
        return input;
    }

    public static int getImagePageCount(Source<?> source) {
        try {
            try (ImageInputStream is = ImageIO.createImageInputStream(source.getSeekableSource().asNewInputStream())) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(is);

                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    reader.setInput(is);
                    try {
                        return reader.getNumImages(true);
                    } finally {
                        reader.dispose();
                    }
                }
            }
        } catch (IOException ex) {
            LOG.warn("Could not determine image page count: {}", source.getName(), ex);
        }

        return 1;
    }

    public PDDocumentHandler getDocumentHandler() {
        return documentHandler;
    }
}
