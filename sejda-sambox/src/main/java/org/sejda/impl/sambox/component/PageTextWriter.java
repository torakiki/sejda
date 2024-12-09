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

import org.sejda.commons.util.StringUtils;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.UnsupportedTextException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageContentStream.AppendMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.graphics.color.PDColor;
import org.sejda.sambox.pdmodel.graphics.color.PDDeviceRGB;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;
import org.sejda.sambox.util.BidiUtils;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Component capable of writing text to a pdf page
 * 
 * @author Andrea Vacondio
 */
public class PageTextWriter {

    private static final Logger LOG = LoggerFactory.getLogger(PageTextWriter.class);

    private PDDocument document;
    // TODO define as a params member
    public static final Float DEFAULT_MARGIN = 20F;

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
            label = BidiUtils.visualToLogical(org.sejda.core.support.util.StringUtils.shapeArabicIf(label));
            
            List<TextWithFont> resolvedStringsToFonts = FontUtils.resolveFonts(label, font, document);
            float stringWidth = 0.0f;
            for (TextWithFont stringAndFont : resolvedStringsToFonts) {
                String s = stringAndFont.getText();
                PDFont f = stringAndFont.getFont();
                stringWidth += f.getStringWidth(s) * fontSize.floatValue() / 1000f;
            }

            PDRectangle pageSize = page.getCropBox().rotate(page.getRotation());
            Point2D position = new Point2D.Double(hAlign.position(pageSize.getWidth(), stringWidth, DEFAULT_MARGIN),
                    vAlign.position(pageSize.getHeight(), DEFAULT_MARGIN, fontSize.floatValue()));

            write(page, position, label, font, fontSize, color);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing the header or footer of the page.", e);
        }
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font, Double fontSize, Color color)
            throws TaskIOException {
        write(page, position, rawLabel, font, fontSize, toPDColor(color));
    }

    public static PDColor toPDColor(Color color) {
        float[] components = new float[] { color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f };
        return new PDColor(components, PDDeviceRGB.INSTANCE);
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font, Double fontSize, PDColor color)
            throws TaskIOException {
        write(page, position, rawLabel, font, fontSize, color, RenderingMode.FILL, false);
    }

    public void write(PDPage page, Point2D position, String rawLabel, PDFont font, Double fontSize, PDColor color,
            RenderingMode renderingMode, boolean fauxItalic) throws TaskIOException {

        String label = StringUtils.normalizeWhitespace(rawLabel);
        label = org.sejda.core.support.util.StringUtils.shapeArabicIf(label);

        List<TextWithFont> resolvedStringsToFonts = FontUtils.resolveFonts(label, font, document);
        int offset = 0;

        PDRectangle pageSize = page.getMediaBox().rotate(page.getRotation());

        // cropped docs have an offset between crop and media box that needs to be taken into account
        PDRectangle mediaSize = page.getMediaBox();
        PDRectangle cropSize = page.getCropBox();
        double cropOffsetX = cropSize.getLowerLeftX();
        double cropOffsetY = cropSize.getLowerLeftY();

        // adjust for rotation
        if (page.getRotation() == 90) {
            cropOffsetX = cropSize.getLowerLeftY();
            cropOffsetY = mediaSize.getUpperRightX() - cropSize.getUpperRightX();
        } else if (page.getRotation() == 180) {
            cropOffsetX = mediaSize.getUpperRightX() - cropSize.getUpperRightX();
            cropOffsetY = mediaSize.getUpperRightY() - cropSize.getUpperRightY();
        } else if (page.getRotation() == 270) {
            cropOffsetX = mediaSize.getUpperRightY() - cropSize.getUpperRightY();
            cropOffsetY = cropSize.getLowerLeftX();
        }

        LOG.trace("media: {} crop: {}", mediaSize, cropSize);
        LOG.trace("offsets: {}, {} and rotation {}", cropOffsetX, cropOffsetY, page.getRotation());

        position = new Point((int) position.getX() + (int) cropOffsetX, (int) position.getY() + (int) cropOffsetY);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true,
                true)) {
            contentStream.beginText();
            contentStream.setTextRenderingMode(renderingMode);
            contentStream.setNonStrokingColor(color);

            for (TextWithFont stringAndFont : resolvedStringsToFonts) {
                try {
                    PDFont resolvedFont = stringAndFont.getFont();

                    double resolvedFontSize = fontSize;
                    String resolvedLabel = stringAndFont.getText();

                    if (resolvedFont == null) {
                        throw new UnsupportedTextException(
                                "Unable to find suitable font for string \"" + StringUtils.asUnicodes(resolvedLabel)
                                        + "\"", resolvedLabel);
                    }
                    
                    for(String textFragment: FontUtils.resolveTextFragments(resolvedLabel, resolvedFont)) {
                        Point2D resolvedPosition = new Point((int) position.getX() + offset, (int) position.getY());

                        contentStream.setFont(resolvedFont, (float) resolvedFontSize);

                        Matrix textMatrix;
                        if (page.getRotation() > 0) {
                            LOG.trace("Unrotated position {}", resolvedPosition);
                            Point2D rotatedPosition = findPositionInRotatedPage(page.getRotation(), pageSize,
                                    resolvedPosition);

                            LOG.trace("Will write string '{}' using font {} at position {}", textFragment,
                                    resolvedFont.getName(), rotatedPosition);

                            AffineTransform tx = AffineTransform.getTranslateInstance(rotatedPosition.getX(),
                                    rotatedPosition.getY());
                            tx.rotate(Math.toRadians(page.getRotation()));
                            textMatrix = new Matrix(tx);

                        } else {
                            LOG.trace("Will write string '{}' using font {} at position {}", textFragment,
                                    resolvedFont.getName(), resolvedPosition);

                            textMatrix = new Matrix(AffineTransform
                                    .getTranslateInstance(resolvedPosition.getX(), resolvedPosition.getY()));
                        }

                        if (fauxItalic) {
                            AffineTransform at = AffineTransform.getShearInstance(0.35, 0);
                            textMatrix.concatenate(new Matrix(at));
                        }

                        contentStream.setTextMatrix(textMatrix);

                        LOG.trace("Text position {}", resolvedPosition);
                        contentStream.showText(textFragment);

                        double textWidth = FontUtils.getSimpleStringWidth(textFragment, resolvedFont, fontSize);

                        offset += textWidth;
                    }
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

    /**
     * Calculates the string's width.
     * 
     * @throws TaskIOException
     */
    public int getStringWidth(String rawLabel, PDFont font, float fontSize) throws TaskIOException {
        String label = StringUtils.normalizeWhitespace(rawLabel);

        List<TextWithFont> resolvedStringsToFonts = FontUtils.resolveFonts(label, font, document);
        int offset = 0;
        for (TextWithFont stringAndFont : resolvedStringsToFonts) {
            try {
                PDFont resolvedFont = stringAndFont.getFont();
                String resolvedLabel = stringAndFont.getText();

                if (nonNull(resolvedFont)) {
                    double textWidth = FontUtils.getSimpleStringWidth(resolvedLabel, resolvedFont, fontSize);

                    offset += textWidth;
                }
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

    public static void writeHeader(PDDocument doc, PDPage page, String text) throws TaskIOException {
        PageTextWriter writer = new PageTextWriter(doc);
        writer.write(page, HorizontalAlign.CENTER, VerticalAlign.TOP, text, PDType1Font.HELVETICA(), 10d, Color.black);
    }
}
