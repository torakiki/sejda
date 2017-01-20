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
import static java.util.Objects.nonNull;
import static org.sejda.impl.sambox.util.FontUtils.canDisplay;
import static org.sejda.impl.sambox.util.FontUtils.findFontFor;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sejda.core.support.util.StringUtils;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageContentStream.AppendMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
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
            String label = StringUtils.normalizeWhitespace(rawLabel);
            List<TextWithFont> resolvedStringsToFonts = resolveFonts(label, font);
            float stringWidth = 0.0f;
            for(TextWithFont stringAndFont: resolvedStringsToFonts) {
                String s = stringAndFont.getText();
                PDFont f = stringAndFont.getFont();
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

        String label = StringUtils.normalizeWhitespace(rawLabel);

        List<TextWithFont> resolvedStringsToFonts = resolveFonts(label, font);
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

        LOG.trace("media: {} crop: {}", mediaSize, cropSize);
        LOG.trace("offsets: {}, {} and rotation", cropOffsetX, cropOffsetY, page.getRotation());

        position = new Point((int) position.getX() + (int)cropOffsetX, (int)position.getY() + (int)cropOffsetY);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true,
                true)) {
            contentStream.beginText();
            contentStream.setTextRenderingMode(renderingMode);
            contentStream.setNonStrokingColor(color);

            for (TextWithFont stringAndFont : resolvedStringsToFonts) {
                try {
                    PDFont resolvedFont = stringAndFont.getFont();
                    String resolvedLabel = stringAndFont.getText();
                    double resolvedFontSize = fontSize;

                    // some fonts don't have glyphs for space. figure out if that's the case and switch to a standard font as fallback
                    if(resolvedLabel.equals(" ")) {
                        if(!FontUtils.canDisplaySpace(resolvedFont)) {
                            resolvedFont = FontUtils.getStandardType1Font(StandardType1Font.HELVETICA);
                        }
                    }

                    // when switching from one font to the other (eg: some letters aren't supported by the original font)
                    // letter size might vary. try to find the best fontSize for the new font so that it matches the height of
                    // the previous letter
                    if (resolvedFont != font) {
                        if(nonNull(font.getFontDescriptor()) &&
                                nonNull(resolvedFont) && nonNull(resolvedFont.getFontDescriptor())) {
                            try {
                                if(font.getFontDescriptor() != null && font.getFontDescriptor().getFontBoundingBox() != null &&
                                        resolvedFont.getFontDescriptor() != null && resolvedFont.getFontDescriptor().getFontBoundingBox() != null) {
                                    double desiredLetterHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
                                    double actualLetterHeight = resolvedFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

                                    resolvedFontSize = fontSize / (actualLetterHeight / desiredLetterHeight);
                                    LOG.debug("Fallback font size calculation: desired vs actual heights: {} vs {}, original vs calculated font size: {} vs {}", desiredLetterHeight, actualLetterHeight, fontSize, resolvedFontSize);
                                }
                            } catch (Exception e) {
                                LOG.warn("Could not calculate fallback font size", e);
                            }
                        }
                    }

                    Point2D resolvedPosition = new Point((int) position.getX() + offset, (int) position.getY());

                    contentStream.setFont(resolvedFont, (float) resolvedFontSize);

                    if (page.getRotation() > 0) {
                        LOG.trace("Unrotated position {}", resolvedPosition);
                        Point2D rotatedPosition = findPositionInRotatedPage(page.getRotation(), pageSize, resolvedPosition);

                        LOG.trace("Will write string '{}' using font {} at position {}", resolvedLabel,
                                resolvedFont.getName(), rotatedPosition);

                        AffineTransform tx = AffineTransform.getTranslateInstance(rotatedPosition.getX(), rotatedPosition.getY());
                        tx.rotate(Math.toRadians(page.getRotation()));
                        contentStream.setTextMatrix(new Matrix(tx));

                    } else {
                        LOG.trace("Will write string '{}' using font {} at position {}", resolvedLabel,
                                resolvedFont.getName(), resolvedPosition);

                        contentStream.setTextMatrix(
                                new Matrix(AffineTransform.getTranslateInstance(resolvedPosition.getX(), resolvedPosition.getY())));
                    }

                    LOG.trace("Text position {}", resolvedPosition);
                    contentStream.showText(resolvedLabel);

                    // sometimes the string width is reported incorrectly, too small. when writing ' ' (space) it leads to missing spaces.
                    // use the largest value between font average width and text string width
                    double textWidth = Math.max(resolvedFont.getAverageFontWidth(), resolvedFont.getStringWidth(resolvedLabel)) / 1000 * fontSize;
                    offset += textWidth;
                } catch (IOException e) {
                    throw new TaskIOException("An error occurred writing text to the page.", e);
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
            throw new TaskIOException("Unable to find suitable font for the given label \"" + StringUtils.asUnicodes(label) + "\"");
        }
        return latestSuitablefont;
    }

    public static class TextWithFont {
        private final PDFont font;
        private final String text;

        public TextWithFont(String text, PDFont font) {
            this.font = font;
            this.text = text;
        }

        public PDFont getFont() {
            return font;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Supports writing labels which require multiple fonts (eg: mixing thai and english words)
     * Returns a list of text with associated font.
     *
     * @param label
     * @param font
     * @return
     * @throws TaskIOException
     */
    List<TextWithFont> resolveFonts(String label, PDFont font) throws TaskIOException {
        PDFont currentFont = font;
        StringBuilder currentString = new StringBuilder();

        // we want to keep the insertion order
        List<TextWithFont> result = new ArrayList<>();

        Iterator<Integer> codePointIterator = label.codePoints().iterator();
        while(codePointIterator.hasNext()) {
            int codePoint = codePointIterator.next();

            String s = new String(Character.toChars(codePoint));

            PDFont f = resolveFont(s, font);
            if(s.equals(" ")) {
                // we want space to be a separate text item
                // because some fonts are missing the space glyph
                // so we'll handle it separate from the other chars
                if(currentString.length() > 0) {
                    result.add(new TextWithFont(currentString.toString(), currentFont));
                }
                result.add(new TextWithFont(" ", currentFont));
                currentString = new StringBuilder();
                currentFont = f;
            } else if(currentFont == f) {
                currentString.append(s);
            } else {
                if(currentString.length() > 0) {
                    result.add(new TextWithFont(currentString.toString(), currentFont));
                }

                currentString = new StringBuilder(s);
                currentFont = f;
            }
        }

        for(TextWithFont each: result) {
            LOG.trace("Will write '{}' with {}", each.getText(), each.getFont());
        }

        result.add(new TextWithFont(currentString.toString(), currentFont));

        return result;
    }

    /**
     * Calculates the string's width, using the same algorithms to resolve fallback fonts as the write() method.
     * @throws TaskIOException
     */
    public int getStringWidth(String rawLabel, PDFont font, float fontSize) throws TaskIOException {
        String label = StringUtils.normalizeWhitespace(rawLabel);

        List<TextWithFont> resolvedStringsToFonts = resolveFonts(label, font);
        int offset = 0;
        for (TextWithFont stringAndFont : resolvedStringsToFonts) {
            try {
                PDFont resolvedFont = stringAndFont.getFont();
                String resolvedLabel = stringAndFont.getText();
                double resolvedFontSize = fontSize;

                // some fonts don't have glyphs for space. figure out if that's the case and switch to a standard font as fallback
                if(resolvedLabel.equals(" ")) {
                    if(!FontUtils.canDisplaySpace(resolvedFont)) {
                        resolvedFont = FontUtils.getStandardType1Font(StandardType1Font.HELVETICA);
                    }
                }

                // when switching from one font to the other (eg: some letters aren't supported by the original font)
                // letter size might vary. try to find the best fontSize for the new font so that it matches the height of
                // the previous letter
                if (resolvedFont != font) {
                    if(nonNull(font.getFontDescriptor()) &&
                            nonNull(resolvedFont) && nonNull(resolvedFont.getFontDescriptor())) {
                        try {
                            if(font.getFontDescriptor() != null && font.getFontDescriptor().getFontBoundingBox() != null &&
                                    resolvedFont.getFontDescriptor() != null && resolvedFont.getFontDescriptor().getFontBoundingBox() != null) {
                                double desiredLetterHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
                                double actualLetterHeight = resolvedFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

                                resolvedFontSize = fontSize / (actualLetterHeight / desiredLetterHeight);
                                LOG.debug("Fallback font size calculation: desired vs actual heights: {} vs {}, original vs calculated font size: {} vs {}", desiredLetterHeight, actualLetterHeight, fontSize, resolvedFontSize);
                            }
                        } catch (Exception e) {
                            LOG.warn("Could not calculate fallback font size", e);
                        }
                    }
                }

                // sometimes the string width is reported incorrectly, too small. when writing ' ' (space) it leads to missing spaces.
                // use the largest value between font average width and text string width
                double textWidth = Math.max(resolvedFont.getAverageFontWidth(), resolvedFont.getStringWidth(resolvedLabel)) / 1000 * fontSize;
                offset += textWidth;
            } catch (IOException e) {
                throw new TaskIOException("An error occurred writing text to the page.", e);
            }
        }

        return offset;
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
