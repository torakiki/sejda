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

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBufferWithName;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class ImagesToPdfDocumentConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesToPdfDocumentConverter.class);

    private PDRectangle defaultPageSize = PDRectangle.A4;
    private boolean shouldPageSizeMatchImageSize = false;
    private PageOrientation pageOrientation = PageOrientation.AUTO;
    private float marginInches = 0f;

    public PDDocumentHandler convert(List<Source<?>> sourceList) throws TaskException {
        return convert(sourceList, new ArrayList<>());
    }

    public PDDocumentHandler convert(List<Source<?>> sourceList, List<PDRectangle> pageSizeList) throws TaskException {
        PDDocumentHandler documentHandler = new PDDocumentHandler();
        documentHandler.setCreatorOnPDDocument();

        PageImageWriter imageWriter = new PageImageWriter(documentHandler.getUnderlyingPDDocument());

        for (int i = 0; i < sourceList.size(); i++) {
            Source<?> source = sourceList.get(i);
            beforeImage(source);
            try {
                PDImageXObject image = PageImageWriter.toPDXImageObject(source);
                PDRectangle mediaBox = defaultPageSize;
                if (!pageSizeList.isEmpty()) {
                    mediaBox = pageSizeList.get(i);
                }
                if (shouldPageSizeMatchImageSize) {
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

                // full page (scaled down only)
                float width = image.getWidth();
                float height = image.getHeight();

                if (width > mediaBox.getWidth()) {
                    int targetWidth = (int) mediaBox.getWidth();
                    LOG.debug("Scaling image down to fit by width {} vs {}", width, targetWidth);

                    float ratio = width / targetWidth;
                    width = targetWidth;
                    height = Math.round(height / ratio);
                }

                if (height > mediaBox.getHeight()) {
                    int targetHeight = (int) mediaBox.getHeight();
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
                float y = ((int) mediaBox.getHeight() - height) / 2;

                imageWriter.append(page, image, new Point((int) x, (int) y), width, height, null, 0);
                int rotation = ExifHelper.getRotationBasedOnExifOrientation(source);
                page.setRotation(rotation);

                afterImage(image);
            } catch (TaskIOException e) {
                failedImage(source, e);
            }
        }

        return documentHandler;
    }

    public void beforeImage(Source<?> source) throws TaskException {

    }

    public void afterImage(PDImageXObject image) throws TaskException {

    }

    public void failedImage(Source<?> source, TaskIOException e) throws TaskException {
        throw e;
    }

    public void setDefaultPageSize(PageSize defaultPageSize) {
        this.defaultPageSize = new PDRectangle(defaultPageSize.getWidth(), defaultPageSize.getHeight());
    }

    public void setPageSize(PDRectangle pageSize) {
        this.defaultPageSize = pageSize;
    }

    public void setShouldPageSizeMatchImageSize(boolean shouldPageSizeMatchImageSize) {
        this.shouldPageSizeMatchImageSize = shouldPageSizeMatchImageSize;
    }

    public void setPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    public void setMarginInches(float marginInches) {
        this.marginInches = marginInches;
    }

    public static void convertImageMergeInputToPdf(BaseMergeParameters<MergeInput> parameters,
            TaskExecutionContext context) throws TaskException {
        // if images were supplied, convert them to PDF
        List<MergeInput> newInputList = new ArrayList<>();
        for (MergeInput input : parameters.getInputList()) {
            if (input instanceof ImageMergeInput) {
                // collect all consecutive images and convert them to a PDF document
                newInputList.add(convertImagesToPdfMergeInput((ImageMergeInput) input, context));
            } else {
                newInputList.add(input);
            }
        }

        parameters.setInputList(newInputList);
    }

    private static PdfMergeInput convertImagesToPdfMergeInput(ImageMergeInput image, TaskExecutionContext context)
            throws TaskException {
        List<Source<?>> sources = Collections.singletonList(image.getSource());
        ImagesToPdfDocumentConverter converter = new ImagesToPdfDocumentConverter() {
            @Override
            public void failedImage(Source<?> source, TaskIOException e) throws TaskException {
                context.assertTaskIsLenient(e);
                notifyEvent(context.notifiableTaskMetadata()).taskWarning(
                        String.format("Image %s was skipped, could not be processed", source.getName()), e);
            }
        };

        converter.setDefaultPageSize(image.getPageSize());
        converter.setShouldPageSizeMatchImageSize(image.isShouldPageSizeMatchImageSize());
        converter.setPageOrientation(image.getPageOrientation());

        PDDocumentHandler converted = converter.convert(sources);
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
}
