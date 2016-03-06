/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;
import static org.sejda.impl.sambox.util.FontUtils.getStandardType1Font;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Closeable;
import java.io.IOException;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.TextStampPattern;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component adding header or footer to PDF doc.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetHeaderFooterWriter implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(SetHeaderFooterWriter.class);

    // TODO define as a params member
    private static final Float DEFAULT_MARGIN = 30F;

    private PDDocumentHandler documentHandler;
    private int totalPages;

    /**
     * @param documentHandler
     *            the document handler holding the document where we want to write the footer
     */
    public SetHeaderFooterWriter(PDDocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
        this.totalPages = documentHandler.getNumberOfPages();
    }

    public void write(SetHeaderFooterParameters parameters, int currentFileCounter) throws TaskIOException {
        PDFont font = defaultIfNull(getStandardType1Font(parameters.getFont()), PDType1Font.HELVETICA);
        Double fontSize = defaultIfNull(parameters.getFontSize(), 10d);

        HorizontalAlign hAlign = defaultIfNull(parameters.getHorizontalAlign(), HorizontalAlign.CENTER);
        VerticalAlign vAlign = defaultIfNull(parameters.getVerticalAlign(), VerticalAlign.BOTTOM);
        String what = vAlign == VerticalAlign.BOTTOM ? "footer" : "header";

        SortedSet<Integer> pages = parameters.getPageRange().getPages(totalPages);
        Integer labelPageNumber = parameters.getPageCountStartFrom();

        for (int pageNumber : pages) {
            // if user didn't override it, use document actual page numbering
            if (labelPageNumber == null) {
                labelPageNumber = pageNumber;
            }

            String batesSeq = null;
            if (parameters.getBatesSequence() != null) {
                batesSeq = parameters.getBatesSequence().next();
            }

            String label = new TextStampPattern().withPage(labelPageNumber, totalPages).withBatesSequence(batesSeq)
                    .withFileSequence(String.valueOf(currentFileCounter)).build(parameters.getPattern());

            // check the label can be written with the selected font. Fallback to matching unicode font otherwise. Try Unicode Serif as last resort.
            // Type 1 fonts only support 8-bit code points.
            font = fontOrFallback(label, font,
                    () -> FontUtils.findFontFor(documentHandler.getUnderlyingPDDocument(), label));
            requireNotNullArg(font, "Unable to find suitable font for the given label");

            LOG.debug("Applying {} '{}' to document page {}", what, label, pageNumber);
            PDPage page = documentHandler.getPage(pageNumber);
            PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());

            try {
                float stringWidth = font.getStringWidth(label) * fontSize.floatValue() / 1000f;
                Point2D position = new Point2D.Float(hAlign.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN),
                        vAlign.position(pageSize.getHeight(), DEFAULT_MARGIN));

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        documentHandler.getUnderlyingPDDocument(), page, true, true)) {
                    contentStream.beginText();
                    contentStream.setFont(font, fontSize.floatValue());
                    contentStream.setNonStrokingColor(parameters.getColor());

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

            labelPageNumber++;
        }
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

    @Override
    public void close() {
        IOUtils.closeQuietly(documentHandler);
    }
}
