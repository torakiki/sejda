/*
 * Created on 15 nov 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
 * Copyright 2017 by Edi Weissmann (edi.weissmann@gmail.com).
 *
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

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.scale.Margins;
import org.sejda.model.scale.PageNormalizationPolicy;
import org.sejda.model.scale.ScaleType;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSFloat;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDPageContentStream.AppendMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLine;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;
import static org.sejda.impl.sambox.component.OutlineUtils.pageGroupedOutlinePageDestinations;
import static org.sejda.model.scale.Margins.inchesToPoints;

/**
 * Component capable of scaling pages or pages content
 *
 * @author Andrea Vacondio
 * @author Eduard Weissmann
 */
public class PdfScaler {
    private static final Logger LOG = LoggerFactory.getLogger(PdfScaler.class);

    private final ScaleType type;

    public PdfScaler(ScaleType type) {
        requireNotNullArg(type, "Scale type cannot be null");
        this.type = type;
    }

    /**
     * @deprecated use {@link PdfScaler#scalePages(PDDocument, PageNormalizationPolicy)}
     */
    @Deprecated
    public void scalePages(PDDocument doc) throws TaskIOException {
        scalePages(doc, PageNormalizationPolicy.SAME_WIDTH_ORIENTATION_BASED);
    }

    /**
     * @deprecated use {@link PdfScaler#scalePages(PDDocument, Iterable, PDRectangle, PageNormalizationPolicy)}
     */
    public void scalePages(PDDocument doc, Iterable<PDPage> pages, PDRectangle targetBox) throws TaskIOException {
        scalePages(doc, pages, targetBox, PageNormalizationPolicy.SAME_WIDTH_ORIENTATION_BASED);
    }

    /**
     * Scales all pages in the doc based on the given {@link PageNormalizationPolicy}
     */
    public void scalePages(PDDocument doc, PageNormalizationPolicy pageNormalization) throws TaskIOException {
        if (PageNormalizationPolicy.NONE != pageNormalization) {
            var firstPage = doc.getPage(0);
            PDRectangle targetBox = firstPage.getCropBox().rotate(firstPage.getRotation());
            scalePages(doc, doc.getPages(), targetBox, pageNormalization);
        }
    }

    /**
     * Scales all the given pages based on the given {@link PageNormalizationPolicy}
     */
    public void scalePages(PDDocument doc, Iterable<PDPage> pages, PDRectangle targetBox,
            PageNormalizationPolicy pageNormalization) throws TaskIOException {
        if (PageNormalizationPolicy.NONE != pageNormalization) {
            LOG.debug("Normalizing pages with policy {}", pageNormalization);
            for (PDPage page : pages) {
                PDRectangle cropBox = page.getCropBox().rotate(page.getRotation());
                double scale = getScalingFactor(targetBox, cropBox, pageNormalization);
                LOG.debug("Scaling page from {} to {}, factor of {}", cropBox, targetBox, scale);
                scale(doc, page, scale);
            }
        }
    }

    public void scale(PDDocument doc, double scale) throws TaskIOException {
        scale(doc, doc.getPages(), scale);
    }

    public void scale(PDDocument doc, PDPage page, double scale) throws TaskIOException {
        scale(doc, Collections.singletonList(page), scale);
    }

    public void scale(PDDocument doc, Iterable<PDPage> pages, double scale) throws TaskIOException {
        if (scale != 1) {
            doScale(doc, pages, scale);
        }
    }

    private void doScale(PDDocument doc, Iterable<PDPage> pages, double scale) throws TaskIOException {
        Set<COSDictionary> processedAnnots = new HashSet<>();
        Map<PDPage, Set<PDPageDestination>> groupedOutline = pageGroupedOutlinePageDestinations(doc);
        for (PDPage page : pages) {
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.PREPEND, true)) {
                Matrix matrix = getMatrix(scale, page.getCropBox(), page.getCropBox());
                contentStream.transform(matrix);
                if (ScaleType.PAGE == type) {
                    scalePageBoxes(scale, page);
                } else {
                    scaleContentBoxes(scale, page);
                }
                transformAnnotations(page, matrix, processedAnnots, doc);

                // adjust outline destination coordinates for all the outline items pointing to this page
                ofNullable(groupedOutline.get(page)).ifPresent(g -> g.forEach(d -> d.transform(matrix)));
            } catch (IOException e) {
                throw new TaskIOException("An error occurred writing scaling the page.", e);
            }
        }
    }

    private static void transformAnnotations(PDPage page, Matrix transform, Set<COSDictionary> processedAnnots,
            PDDocument doc) {
        page.getAnnotations().stream().filter(a -> !processedAnnots.contains(a.getCOSObject())).forEach(a -> {

            // set the new rectangle
            ofNullable(a.getRectangle()).map(r -> r.transform(transform).getBounds2D()).map(PDRectangle::new)
                    .ifPresent(a::setRectangle);

            // Text Markup, Link and Redaction annotations can have quadpoints
            ofNullable(a.getCOSObject().getDictionaryObject(COSName.QUADPOINTS, COSArray.class)).filter(
                            p -> p.size() == 8).map(COSArray::toFloatArray)
                    .ifPresent(f -> a.getCOSObject().setItem(COSName.QUADPOINTS, transformPoints(f, transform)));

            // adjust destination coordinates
            if (a instanceof PDAnnotationLink) {
                try {
                    ((PDAnnotationLink) a).resolveToPageDestination(doc.getDocumentCatalog())
                            .ifPresent(d -> d.transform(transform));
                } catch (IOException e) {
                    LOG.warn("Unable to process link annotation destination", e);
                }
            }

            // adjust line length
            if (a instanceof PDAnnotationLine) {
                ofNullable(((PDAnnotationLine) a).getLine()).filter(p -> p.length == 4)
                        .ifPresent(f -> a.getCOSObject().setItem(COSName.L, transformPoints(f, transform)));
            }

            // adjust Free Text CL
            ofNullable(a.getCOSObject().getDictionaryObject(COSName.CL, COSArray.class)).filter(p -> p.size() % 2 == 0)
                    .map(COSArray::toFloatArray)
                    .ifPresent(f -> a.getCOSObject().setItem(COSName.CL, transformPoints(f, transform)));

            // Polygon and Polyline vertices
            ofNullable(a.getCOSObject().getDictionaryObject(COSName.VERTICES, COSArray.class)).filter(
                            p -> p.size() % 2 == 0).map(COSArray::toFloatArray)
                    .ifPresent(f -> a.getCOSObject().setItem(COSName.VERTICES, transformPoints(f, transform)));
            processedAnnots.add(a.getCOSObject());
        });
    }

    private static COSArray transformPoints(float[] points, Matrix transform) {
        COSArray newPoints = new COSArray();
        for (int i = 0; i < points.length; i++) {
            Float newPoint = transform.transformPoint(points[i], points[++i]);
            newPoints.add(new COSFloat(newPoint.x));
            newPoints.add(new COSFloat(newPoint.y));
        }
        return newPoints;
    }

    /**
     * Adds the given margin all around the pages
     */
    public static void margin(PDDocument doc, Iterable<PDPage> pages, Margins margins) throws TaskIOException {
        if (nonNull(margins)) {
            Map<PDPage, Set<PDPageDestination>> groupedOutline = pageGroupedOutlinePageDestinations(doc);
            Set<COSDictionary> processedAnnots = new HashSet<>();
            for (PDPage page : pages) {
                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.PREPEND, true)) {
                    page.setCropBox(addMargins(page.getCropBox().rotate(page.getRotation()), margins).rotate(
                            -page.getRotation()));
                    page.setMediaBox(addMargins(page.getMediaBox().rotate(page.getRotation()), margins).rotate(
                            -page.getRotation()));
                    ofNullable(page.getBleedBoxRaw()).ifPresent(r -> page.setBleedBox(
                            addMargins(r.rotate(page.getRotation()), margins).rotate(-page.getRotation())));
                    ofNullable(page.getTrimBoxRaw()).ifPresent(r -> page.setTrimBox(
                            addMargins(r.rotate(page.getRotation()), margins).rotate(-page.getRotation())));
                    ofNullable(page.getArtBoxRaw()).ifPresent(r -> page.setArtBox(
                            addMargins(r.rotate(page.getRotation()), margins).rotate(-page.getRotation())));

                    Matrix matrix = new Matrix(AffineTransform.getTranslateInstance(inchesToPoints(margins.left),
                            inchesToPoints(margins.bottom)));
                    // realign the content
                    contentStream.transform(matrix);

                    transformAnnotations(page, matrix, processedAnnots, doc);
                    // adjust outline destination coordinates for all the outline items pointing to this page
                    ofNullable(groupedOutline.get(page)).ifPresent(g -> g.forEach(d -> d.transform(matrix)));
                } catch (IOException e) {
                    throw new TaskIOException("An error occurred adding margins to the page.", e);
                }
            }
        }
    }

    private static PDRectangle addMargins(PDRectangle rect, Margins margins) {
        return new PDRectangle(rect.getLowerLeftX(), rect.getLowerLeftY(),
                (float) (rect.getWidth() + inchesToPoints(margins.left) + inchesToPoints(margins.right)),
                (float) (rect.getHeight() + inchesToPoints(margins.top) + inchesToPoints(margins.bottom)));
    }

    private Matrix getMatrix(double scale, PDRectangle crop, PDRectangle toScale) {
        if (ScaleType.CONTENT == type) {
            AffineTransform transform = AffineTransform.getTranslateInstance(
                    (crop.getWidth() - (toScale.getWidth() * scale)) / 2,
                    (crop.getHeight() - (toScale.getHeight() * scale)) / 2);
            transform.scale(scale, scale);
            return new Matrix(transform);
        }
        return new Matrix(AffineTransform.getScaleInstance(scale, scale));
    }

    private void scaleContentBoxes(double scale, PDPage page) {
        PDRectangle cropBox = page.getCropBox();
        // we adjust art and bleed same as Acrobat does
        if (scale > 1) {
            page.setBleedBox(cropBox);
            page.setTrimBox(cropBox);
        } else {
            page.setBleedBox(new PDRectangle(
                    page.getBleedBox().transform(getMatrix(scale, page.getCropBox(), page.getBleedBox()))
                            .getBounds2D()));
            page.setTrimBox(new PDRectangle(
                    page.getTrimBox().transform(getMatrix(scale, page.getCropBox(), page.getTrimBox())).getBounds2D()));
        }
        Rectangle2D newArt = page.getArtBox().transform(getMatrix(scale, page.getCropBox(), page.getArtBox()))
                .getBounds2D();
        if (newArt.getX() < cropBox.getLowerLeftX() || newArt.getY() < cropBox.getLowerLeftX()) {
            // we overlow the cropbox
            page.setArtBox(page.getCropBox());
        } else {
            page.setArtBox(new PDRectangle(newArt));
        }
    }

    private void scalePageBoxes(double scale, PDPage page) {
        page.setArtBox(new PDRectangle(
                page.getArtBox().transform(getMatrix(scale, page.getCropBox(), page.getArtBox())).getBounds2D()));
        page.setBleedBox(new PDRectangle(
                page.getBleedBox().transform(getMatrix(scale, page.getCropBox(), page.getBleedBox())).getBounds2D()));
        page.setTrimBox(new PDRectangle(
                page.getTrimBox().transform(getMatrix(scale, page.getCropBox(), page.getTrimBox())).getBounds2D()));
        page.setCropBox(new PDRectangle(
                page.getCropBox().transform(getMatrix(scale, page.getCropBox(), page.getCropBox())).getBounds2D()));
        page.setMediaBox(new PDRectangle(
                page.getMediaBox().transform(getMatrix(scale, page.getMediaBox(), page.getMediaBox())).getBounds2D()));
    }

    /**
     * Calculates the scaling factor for pageBox to fit targetBox based on the pageNormalization policy
     * the targetBox.
     */
    private double getScalingFactor(PDRectangle targetBox, PDRectangle pageBox,
            PageNormalizationPolicy pageNormalization) {
        return switch (pageNormalization) {
            case NONE -> 1;
            case SAME_WIDTH -> targetBox.getWidth() / pageBox.getWidth();
            case SAME_WIDTH_ORIENTATION_BASED -> {
                // if both target and page boxes have same orientation (landscape, portrait)
                // the scaling factor is targetWidth / pageWidth
                if (isLandscape(targetBox) == isLandscape(pageBox)) {
                    yield targetBox.getWidth() / pageBox.getWidth();
                }
                // the boxes have different orientations
                // the page should be scaled to match the target box height
                yield targetBox.getHeight() / pageBox.getWidth();
            }
        };

    }

    public static boolean isLandscape(PDRectangle box) {
        return box.getWidth() > box.getHeight();
    }

}
