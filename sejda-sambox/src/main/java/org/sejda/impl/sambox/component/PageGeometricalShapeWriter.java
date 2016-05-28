/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.edit.Shape;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

public class PageGeometricalShapeWriter {
    private PDDocument document;

    public PageGeometricalShapeWriter(PDDocument document) {
        this.document = document;
    }

    public void drawShape(Shape shape, PDPage page, Point2D position, float width, float height,
                          Color borderColor, Color backgroundColor, float borderWidth) throws TaskIOException {
        switch (shape) {
            case RECTANGLE: {
                drawRectangle(page, position, width, height, borderColor, backgroundColor, borderWidth);
                break;
            }
            case ELLIPSE: {
                drawEllipse(page, position, width, height, borderColor, backgroundColor, borderWidth);
                break;
            }
        }
    }

    public void drawRectangle(PDPage page, Point2D position, float width, float height,
                              Color borderColor, Color backgroundColor, float borderWidth) throws TaskIOException {
        try {
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                contentStream.setLineWidth(borderWidth);
                if (backgroundColor != null) {
                    contentStream.setNonStrokingColor(backgroundColor);
                }
                contentStream.setStrokingColor(borderColor);

                contentStream.addRect((float) position.getX(), (float) position.getY(), width, height);
                if(backgroundColor != null) {
                    contentStream.closeAndFillAndStroke();
                } else {
                    contentStream.closeAndStroke();
                }
                contentStream.close();
            }
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing image to the page.", e);
        }
    }

    public void drawEllipse(PDPage page, Point2D position, float width, float height,
                            Color borderColor, Color backgroundColor, float borderWidth) throws TaskIOException {
        try {
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                contentStream.setLineWidth(borderWidth);
                if (backgroundColor != null) {
                    contentStream.setNonStrokingColor(backgroundColor);
                }
                contentStream.setStrokingColor(borderColor);

                float w = width, h = height, x = (float) position.getX(), y = (float) position.getY();

                float kappa = 0.5522848f,
                        ox = (w / 2) * kappa, // control point offset horizontal
                        oy = (h / 2) * kappa, // control point offset vertical
                        xe = x + w,           // x-end
                        ye = y + h,           // y-end
                        xm = x + w / 2,       // x-middle
                        ym = y + h / 2;       // y-middle

                contentStream.moveTo(x, ym);
                contentStream.curveTo(x, ym - oy, xm - ox, y, xm, y);
                contentStream.curveTo(xm + ox, y, xe, ym - oy, xe, ym);
                contentStream.curveTo(xe, ym + oy, xm + ox, ye, xm, ye);
                contentStream.curveTo(xm - ox, ye, x, ym + oy, x, ym);

                if(backgroundColor != null) {
                    contentStream.closeAndFillAndStroke();
                } else {
                    contentStream.closeAndStroke();
                }
                contentStream.close();
            }
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing image to the page.", e);
        }
    }
}
