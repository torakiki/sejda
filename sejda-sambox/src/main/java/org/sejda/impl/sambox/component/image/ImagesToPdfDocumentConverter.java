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

import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageImageWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.Source;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

public class ImagesToPdfDocumentConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesToPdfDocumentConverter.class);

    public PDDocumentHandler convert(List<Source<?>> sourceList) throws TaskException {
        PDDocumentHandler documentHandler = new PDDocumentHandler();
        documentHandler.setCreatorOnPDDocument();

        PageImageWriter imageWriter = new PageImageWriter(documentHandler.getUnderlyingPDDocument());

        for (Source<?> source : sourceList) {
            beforeImage(source);
            try {
                PDImageXObject image = PageImageWriter.toPDXImageObject(source);
                PDRectangle mediaBox = PDRectangle.A4;

                if (image.getWidth() > image.getHeight() && image.getWidth() > mediaBox.getWidth()) {
                    mediaBox = new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth());
                }

                PDPage page = documentHandler.addBlankPage(mediaBox);

                // full page (scaled down only)
                int width = image.getWidth();
                int height = image.getHeight();

                if (width > mediaBox.getWidth()) {
                    int targetWidth = (int) mediaBox.getWidth();
                    LOG.debug("Scaling image down to fit by width {} vs {}", width, targetWidth);

                    float ratio = (float) width / targetWidth;
                    width = targetWidth;
                    height = Math.round(height / ratio);
                }

                if (height > mediaBox.getHeight()) {
                    int targetHeight = (int) mediaBox.getHeight();
                    LOG.debug("Scaling image down to fit by height {} vs {}", height, targetHeight);

                    float ratio = (float) height / targetHeight;
                    height = targetHeight;
                    width = Math.round(width / ratio);
                }

                // centered on page
                int x = ((int) mediaBox.getWidth() - width) / 2;
                int y = ((int) mediaBox.getHeight() - height) / 2;

                imageWriter.append(page, image, new Point(x, y), width, height, null, 0);

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

    }
}
