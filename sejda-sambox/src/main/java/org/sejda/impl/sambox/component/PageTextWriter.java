/*
 * Created on 06 mar 2016
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

import static org.sejda.impl.sambox.util.FontUtils.canDisplay;
import static org.sejda.impl.sambox.util.FontUtils.findFontFor;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageContentStream.AppendMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component capable of writing text to a pdf page
 * 
 * @author Andrea Vacondio
 */
public class PageTextWriter {

    private static final Logger LOG = LoggerFactory.getLogger(PageTextWriter.class);

    private PDDocument document;
    // TODO define as a params member
    private static final Float DEFAULT_MARGIN = 30F;
    private PDFont latestSuitablefont;

    /**
     * @param document
     *            the document where we want to write the footer
     */
    public PageTextWriter(PDDocument document) {
        this.document = document;
    }

    public void write(PDPage page, HorizontalAlign hAlign, VerticalAlign vAlign, String label, PDFont font,
                      Double fontSize, Color color) throws TaskIOException {

        try {
            resolveFont(label, font);

            PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
            float stringWidth = latestSuitablefont.getStringWidth(label) * fontSize.floatValue() / 1000f;
            Point2D position = new Point2D.Float(hAlign.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN),
                vAlign.position(pageSize.getHeight(), DEFAULT_MARGIN - fontSize.floatValue()));

            write(page, position, label, font, fontSize, color);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
        }
    }

    public void write(PDPage page, Point2D position, String label, PDFont font,
            Double fontSize, Color color) throws TaskIOException {

        resolveFont(label, font);

        PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());

        try {
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true,
                    true)) {
                contentStream.beginText();
                contentStream.setFont(latestSuitablefont, fontSize.floatValue());
                contentStream.setNonStrokingColor(color);

                if (page.getRotation() > 0) {
                    position = findPositionInRotatedPage(page.getRotation(), pageSize, position);

                    AffineTransform tx = AffineTransform.getTranslateInstance(position.getX(), position.getY());
                    tx.rotate(Math.toRadians(page.getRotation()));
                    contentStream.setTextMatrix(new Matrix(tx));

                } else {
                    contentStream.setTextMatrix(
                            new Matrix(AffineTransform.getTranslateInstance(position.getX(), position.getY())));
                }

                LOG.trace("Text position {}", position);
                contentStream.showText(label);
                contentStream.endText();
            }
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
        }
    }

    private void resolveFont(String label, PDFont font) {
        // check the label can be written with the selected font. Fallback to matching unicode font otherwise. Try Unicode Serif as last resort.
        // Type 1 fonts only support 8-bit code points.
        latestSuitablefont = fontOrFallback(label, font, () -> {
            if (canDisplay(label, latestSuitablefont)) {
                return latestSuitablefont;
            }
            return findFontFor(document, label);
        });
        requireNotNullArg(latestSuitablefont, "Unable to find suitable font for the given label");
    }

    private Point2D findPositionInRotatedPage(int rotation, PDRectangle pageSize, Point2D position) {
        LOG.debug("Found rotation {}", rotation);
        // flip
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        if (rotation == 90) {
            transform.translate(pageSize.getHeight(), 0);
        }
        if (rotation == 180) {
            transform.translate(pageSize.getWidth(), -pageSize.getHeight());
        }
        if (rotation == 270) {
            transform.translate(0, -pageSize.getWidth());
        }
        transform.rotate(Math.toRadians(-rotation));
        // flip
        transform.scale(1, -1);
        return transform.transform(position, null);
    }
}
