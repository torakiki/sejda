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

import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageContentStream.AppendMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType3Font;
import org.sejda.sambox.pdmodel.graphics.color.PDColor;
import org.sejda.sambox.pdmodel.graphics.color.PDDeviceRGB;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;
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
            String label = normalizeWhitespace(rawLabel);
            LinkedHashMap<String, PDFont> resolvedStringsToFonts = resolveFonts(label, font);
            float stringWidth = 0.0f;
            for(Map.Entry<String, PDFont> stringAndFont: resolvedStringsToFonts.entrySet()) {
                String s = stringAndFont.getKey();
                PDFont f = stringAndFont.getValue();
                stringWidth += f.getStringWidth(s) * fontSize.floatValue() / 1000f;
            }

            PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
            Point2D position = new Point2D.Double(hAlign.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN),
                vAlign.position(pageSize.getHeight(), DEFAULT_MARGIN - fontSize.floatValue()));

            write(page, position, label, font, fontSize, color);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
        }
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font,
                      Double fontSize, Color color) throws TaskIOException {
        float[] components = new float[] { color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f };
        PDColor pdColor = new PDColor(components, PDDeviceRGB.INSTANCE);
        write(page, position, rawLabel, font, fontSize, pdColor);
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font,
                      Double fontSize, PDColor color) throws TaskIOException {
        write(page, position, rawLabel, font, fontSize, color, RenderingMode.FILL);
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font,
            Double fontSize, PDColor color, RenderingMode renderingMode) throws TaskIOException {

        String label = normalizeWhitespace(rawLabel);

        LinkedHashMap<String, PDFont> resolvedStringsToFonts = resolveFonts(label, font);
        int offset = 0;

        PDRectangle pageSize = page.getMediaBox().rotate(page.getRotation());

        // cropped docs have an offset between crop and media box that needs to be taken into account
        PDRectangle mediaSize = page.getMediaBox();
        PDRectangle cropSize = page.getCropBox();
        double cropOffsetX = cropSize.getLowerLeftX();
        double cropOffsetY = cropSize.getLowerLeftY();

        // adjust for rotation
        if(page.getRotation() == 90) {
            cropOffsetX = cropSize.getLowerLeftY();
            cropOffsetY = mediaSize.getUpperRightX() - cropSize.getUpperRightX();
        } else if(page.getRotation() == 180) {
            cropOffsetX = mediaSize.getUpperRightX() - cropSize.getUpperRightX();
            cropOffsetY = mediaSize.getUpperRightY() - cropSize.getUpperRightY();
        } else if(page.getRotation() == 270) {
            cropOffsetX = mediaSize.getUpperRightY() - cropSize.getUpperRightY();
            cropOffsetY = cropSize.getLowerLeftX();
        }

        LOG.debug("media: {} crop: {}", mediaSize, cropSize);
        LOG.debug("offsets: {}, {} and rotation", cropOffsetX, cropOffsetY, page.getRotation());

        position = new Point((int) position.getX() + (int)cropOffsetX, (int)position.getY() + (int)cropOffsetY);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true,
                true)) {
            contentStream.beginText();
            contentStream.setTextRenderingMode(renderingMode);
            contentStream.setNonStrokingColor(color);

            for (Map.Entry<String, PDFont> stringAndFont : resolvedStringsToFonts.entrySet()) {
                try {
                    PDFont resolvedFont = stringAndFont.getValue();
                    String resolvedLabel = stringAndFont.getKey();
                    double resolvedFontSize = fontSize;

                    if(FontUtils.isOnlyWhitespace(resolvedLabel)) {
                        // most of the times subset fonts don't contain space, instead space is simulated by offsetting the next word
                        // so don't write space with a fallback font, just increment the offset
                        float widthOfSpace = calculateWidthOfSpace(resolvedFont);
                        offset += widthOfSpace;
                        // TODO: support for multiple spaces, should the offset be proportional to the number of spaces?
                        LOG.debug("Writing '{}' by offsetting with {}", resolvedLabel, widthOfSpace);
                        continue;
                    }

                    // when switching from one font to the other (eg: some letters aren't supported by the original font)
                    // letter size might vary. try to find the best fontSize for the new font so that it matches the height of
                    // the previous letter
                    if (resolvedFont != font) {
                        double desiredLetterHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
                        double actualLetterHeight = resolvedFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

                        resolvedFontSize = fontSize / (actualLetterHeight / desiredLetterHeight);
                        LOG.debug("Fallback font size calculation: desired vs actual heights: {} vs {}, original vs calculated font size: {} vs {}", desiredLetterHeight, actualLetterHeight, fontSize, resolvedFontSize);
                    }

                    Point2D resolvedPosition = new Point((int) position.getX() + offset, (int) position.getY());

                    contentStream.setFont(resolvedFont, (float) resolvedFontSize);

                    if (page.getRotation() > 0) {
                        LOG.debug("Unrotated position {}", resolvedPosition);
                        Point2D rotatedPosition = findPositionInRotatedPage(page.getRotation(), pageSize, resolvedPosition);

                        LOG.debug("Will write string '{}' using font {} at position {}", resolvedLabel, resolvedFont.getName(), rotatedPosition);

                        AffineTransform tx = AffineTransform.getTranslateInstance(rotatedPosition.getX(), rotatedPosition.getY());
                        tx.rotate(Math.toRadians(page.getRotation()));
                        contentStream.setTextMatrix(new Matrix(tx));

                    } else {
                        LOG.debug("Will write string '{}' using font {} at position {}", resolvedLabel, resolvedFont.getName(), resolvedPosition);

                        contentStream.setTextMatrix(
                                new Matrix(AffineTransform.getTranslateInstance(resolvedPosition.getX(), resolvedPosition.getY())));
                    }

                    LOG.trace("Text position {}", resolvedPosition);
                    contentStream.showText(resolvedLabel);

                    offset += resolvedFont.getStringWidth(resolvedLabel) / 1000 * fontSize;
                } catch (IOException e) {
                    throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
                }
            }

            contentStream.setTextRenderingMode(RenderingMode.FILL);
            contentStream.endText();

        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
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

        Iterator<Integer> codePointIterator = label.codePoints().iterator();
        while(codePointIterator.hasNext()) {
            int codePoint = codePointIterator.next();

            String s = new String(Character.toChars(codePoint));

            if(s.equals(" ")) {
                currentString.append(s);
                continue;
            }

            PDFont f = resolveFont(s, font);
            if(currentFont == f) {
                currentString.append(s);
            } else {
                if(currentString.toString().length() > 0) {
                    result.put(currentString.toString(), currentFont);
                }

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

    private String normalizeWhitespace(String in) {
        // removes control characters like \n, \r or \t
        // replaces all whitespace (eg: &nbsp;) with ' ' (space)
        return in.replaceAll("[\\n\\t\\r]", "").replaceAll("\\p{Z}\\s", " ");
    }

    // taken from PDFTextStreamEngine
    private float calculateWidthOfSpace(PDFont font) {
        float glyphSpaceToTextSpaceFactor = 1 / 1000f;
        if (font instanceof PDType3Font)
        {
            glyphSpaceToTextSpaceFactor = font.getFontMatrix().getScaleX();
        }

        float spaceWidthText = 0;
        try
        {
            // to avoid crash as described in PDFBOX-614, see what the space displacement should be
            spaceWidthText = font.getSpaceWidth() * glyphSpaceToTextSpaceFactor;
        }
        catch (Throwable exception)
        {
            LOG.warn(exception.getMessage(), exception);
        }

        if (spaceWidthText == 0)
        {
            spaceWidthText = font.getAverageFontWidth() * glyphSpaceToTextSpaceFactor;
            // the average space width appears to be higher than necessary so make it smaller
            spaceWidthText *= .80f;
        }
        if (spaceWidthText == 0)
        {
            spaceWidthText = 1.0f; // if could not find font, use a generic value
        }

        return spaceWidthText;
    }
}
