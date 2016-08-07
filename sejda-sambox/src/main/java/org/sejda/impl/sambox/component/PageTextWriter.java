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

import static java.util.Objects.isNull;
import static org.sejda.impl.sambox.util.FontUtils.canDisplay;
import static org.sejda.impl.sambox.util.FontUtils.findFontFor;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

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

    public void write(PDPage page, HorizontalAlign hAlign, VerticalAlign vAlign, String rawLabel, PDFont font,
                      Double fontSize, Color color) throws TaskIOException {

        try {
            String label = removeControlCharacters(rawLabel);
            LinkedHashMap<String, PDFont> resolvedStringsToFonts = resolveFonts(label, font);
            float stringWidth = 0.0f;
            for(Map.Entry<String, PDFont> stringAndFont: resolvedStringsToFonts.entrySet()) {
                String s = stringAndFont.getKey();
                PDFont f = stringAndFont.getValue();
                stringWidth += f.getStringWidth(s) * fontSize.floatValue() / 1000f;
            }

            PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
            Point2D position = new Point2D.Float(hAlign.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN),
                vAlign.position(pageSize.getHeight(), DEFAULT_MARGIN - fontSize.floatValue()));

            write(page, position, label, font, fontSize, color);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
        }
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font,
            Double fontSize, Color color) throws TaskIOException {

        String label = removeControlCharacters(rawLabel);

        LinkedHashMap<String, PDFont> resolvedStringsToFonts = resolveFonts(label, font);
        int offset = 0;

        PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());

        for(Map.Entry<String, PDFont> stringAndFont: resolvedStringsToFonts.entrySet()) {
            try {
                PDFont resolvedFont = stringAndFont.getValue();
                String resolvedLabel = stringAndFont.getKey();
                Point2D resolvedPosition = new Point((int) position.getX() + offset, (int)position.getY());

                //LOG.debug("Will write string {} using font {} at offset {}", resolvedLabel, resolvedFont.getName(), offset);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true,
                        true)) {
                    contentStream.beginText();
                    contentStream.setFont(resolvedFont, fontSize.floatValue());
                    contentStream.setNonStrokingColor(color);

                    if (page.getRotation() > 0) {
                        Point2D rotatedPosition = findPositionInRotatedPage(page.getRotation(), pageSize, resolvedPosition);

                        AffineTransform tx = AffineTransform.getTranslateInstance(rotatedPosition.getX(), rotatedPosition.getY());
                        tx.rotate(Math.toRadians(page.getRotation()));
                        contentStream.setTextMatrix(new Matrix(tx));

                    } else {
                        contentStream.setTextMatrix(
                                new Matrix(AffineTransform.getTranslateInstance(resolvedPosition.getX(), resolvedPosition.getY())));
                    }

                    LOG.trace("Text position {}", resolvedPosition);
                    contentStream.showText(resolvedLabel);
                    contentStream.endText();
                }

                offset += resolvedFont.getStringWidth(resolvedLabel) / 1000 * fontSize;
            } catch (IOException e) {
                throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
            }
        }
    }

    private PDFont resolveFont(String label, PDFont font) throws TaskIOException {
        // check the label can be written with the selected font. Fallback to matching unicode font otherwise. Try Unicode Serif as last resort.
        // Type 1 fonts only support 8-bit code points.
        latestSuitablefont = fontOrFallback(label, font, () -> {
            if (canDisplay(label, latestSuitablefont)) {
                return latestSuitablefont;
            }
            return findFontFor(document, label);
        });
        if (isNull(latestSuitablefont)) {
            throw new TaskIOException("Unable to find suitable font for the given label \"" + label + "\"");
        }
        return latestSuitablefont;
    }

    /**
     * Supports writing labels which require multiple fonts (eg: mixing thai and english words)
     * Returns a map of string to font. Keys are ordered in the same order the strings appear in the original label.
     *
     * @param label
     * @param font
     * @return
     * @throws TaskIOException
     */
    private LinkedHashMap<String, PDFont> resolveFonts(String label, PDFont font) throws TaskIOException {
        PDFont currentFont = font;
        StringBuilder currentString = new StringBuilder();

        // we want to keep the insertion order
        LinkedHashMap<String, PDFont> result = new LinkedHashMap<>();

        for(Character c: label.toCharArray()) {
            String s = c.toString();
            PDFont f = resolveFont(s, font);
            if(isNull(currentFont) || currentFont == f) {
                currentString.append(s);
            } else {
                result.put(currentString.toString(), currentFont);
                currentString = new StringBuilder(s);
                currentFont = f;
            }
        }

        result.put(currentString.toString(), currentFont);

        return result;
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

    private String removeControlCharacters(String in) {
        return in.replaceAll("[\\n\\t\\r]", "");
    }
}
