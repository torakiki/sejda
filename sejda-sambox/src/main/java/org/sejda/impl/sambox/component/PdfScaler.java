/*
 * Created on 15 nov 2016
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
package org.sejda.impl.sambox.component;

import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.scale.ScaleType;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageContentStream.AppendMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.util.Matrix;

/**
 * Component capable of scaling pages or pages content
 * 
 * @author Andrea Vacondio
 *
 */
public class PdfScaler {

    private ScaleType type;

    public PdfScaler(ScaleType type) {
        requireNotNullArg(type, "Scale type cannot be null");
        this.type = type;
    }

    public void scale(PDDocumentHandler documentHandler, double scale) throws TaskIOException {
        if (scale != 1) {
            for (PDPage page : documentHandler.getPages()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(
                        documentHandler.getUnderlyingPDDocument(), page, AppendMode.PREPEND, true)) {
                    PDRectangle crop = page.getCropBox();
                    AffineTransform transform = AffineTransform.getTranslateInstance(
                            (crop.getWidth() - (crop.getWidth() * scale)) / 2,
                            (crop.getHeight() - (crop.getHeight() * scale)) / 2);
                    transform.scale(scale, scale);
                    Matrix matrix = new Matrix(transform);
                    contentStream.transform(matrix);
                    if (ScaleType.PAGE == type) {
                        Rectangle2D newCrop = page.getCropBox().transform(matrix).getBounds2D();
                        page.setCropBox(new PDRectangle((float) newCrop.getX(), (float) newCrop.getY(),
                                (float) newCrop.getWidth(), (float) newCrop.getHeight()));
                        Rectangle2D newMedia = page.getMediaBox().transform(matrix).getBounds2D();
                        page.setMediaBox(new PDRectangle((float) newMedia.getX(), (float) newMedia.getY(),
                                (float) newMedia.getWidth(), (float) newMedia.getHeight()));
                    }
                    scaleBoxes(scale, page, matrix);

                } catch (IOException e) {
                    throw new TaskIOException("An error occurred writing scaling the page.", e);
                }
            }
        }

    }

    private void scaleBoxes(double scale, PDPage page, Matrix transform) {
        PDRectangle cropBox = page.getCropBox();
        // we adjust art and bleed same as Acrobat does
        if (scale > 1) {
            page.setBleedBox(cropBox);
        } else {
            Rectangle2D newBleed = page.getBleedBox().transform(transform).getBounds2D();
            page.setBleedBox(new PDRectangle((float) newBleed.getX(), (float) newBleed.getY(),
                    (float) newBleed.getWidth(), (float) newBleed.getHeight()));
        }
        Rectangle2D newArt = page.getArtBox().transform(transform).getBounds2D();
        if (newArt.getX() < cropBox.getLowerLeftX() || newArt.getY() < cropBox.getLowerLeftX()) {
            // we overlow the cropbox
            page.setArtBox(page.getCropBox());
        } else {
            page.setArtBox(new PDRectangle((float) newArt.getX(), (float) newArt.getY(), (float) newArt.getWidth(),
                    (float) newArt.getHeight()));
        }
    }

}
