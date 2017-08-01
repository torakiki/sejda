/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.awt.*;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.PageSize;
import org.sejda.model.parameter.ResizePagesParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;

/**
 * @author Eduard Weissmann
 *
 */
@Ignore
public abstract class ResizePagesTaskTest extends BaseTaskTest<ResizePagesParameters> {

    @Test
    public void testAddMargins() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/test-pdf.pdf"));
        parameters.setMargin(2);
        parameters.addPageRange(new PageRange(1, 3));

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        // number of pages does not change
        testContext.assertPages(11).forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);

            // page size does not change
            PDRectangle expected = new PDRectangle(0f, 0f, 595f, 842f);
            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());

            // contents is scaled to create margins
            String content = extractText(page, new Rectangle(115, 165, 332, 35));
            assertEquals("Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>",
                    extractText(page, new Rectangle(115, 170, 315, 9)));
            assertEquals("Everyone is permitted to copy and distribute verbatim copies",
                    extractText(page, new Rectangle(115, 179, 315, 9)));
            assertEquals("of this license document, but changing it is not allowed.",
                    extractText(page, new Rectangle(115, 188, 315, 9)));

            page = d.getPage(3);

            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());

            content = extractText(page, new Rectangle(65, 54, 91, 15));
            assertEquals("You may charge", content);
        });
    }

    @Test
    public void landscape() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/landscape.pdf"));
        parameters.addSource(customInput("pdf/landscape_by_rotation.pdf"));
        parameters.setPageSize(PageSize.A5);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);
            assertEqualsRect(PDRectangle.A5.rotate(), page.getCropBox().rotate(page.getRotation()));
            assertEqualsRect(PDRectangle.A5.rotate(), page.getMediaBox().rotate(page.getRotation()));
        });
    }

    @Test
    public void potrait() throws IOException {
        // A4 to A3
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/potrait.pdf"));
        parameters.addSource(customInput("pdf/potrait.pdf"));
        parameters.setPageSize(PageSize.A3);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);
            assertEqualsRect(new PDRectangle(0, 0, 842, 1192), page.getCropBox().rotate(page.getRotation()));
            assertEqualsRect(new PDRectangle(0, 0, 842, 1192), page.getMediaBox().rotate(page.getRotation()));
        });
    }

    private PageSize annotPageSize = PageSize.fromInches(17, 24);

    @Test
    public void annotationsRectangleAndQuadPoints() throws IOException {

        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/highlighted-potrait.pdf"));
        parameters.setPageSize(annotPageSize);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forPdfOutput(d -> {
            d.getPage(0).getAnnotations().forEach(a -> {
                assertEqualsRect(new PDRectangle(117, 1588, 110, 26), a.getRectangle());
                assertArrayEquals(new float[] { 117, 1614, 226, 1614, 117, 1588, 226, 1588 },
                        a.getCOSObject().getDictionaryObject(COSName.QUADPOINTS, COSArray.class).toFloatArray(), 1);
            });

        });
    }

    @Test
    public void annotationsCallout() throws IOException {

        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/callout-potrait.pdf"));
        parameters.setPageSize(annotPageSize);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forPdfOutput(d -> {
            d.getPage(0).getAnnotations().forEach(a -> {
                assertEqualsRect(new PDRectangle(259, 1496, 496, 104), a.getRectangle());
                assertArrayEquals(new float[] { 259, 1596, 507, 1510, 533, 1511 },
                        a.getCOSObject().getDictionaryObject(COSName.CL, COSArray.class).toFloatArray(), 1);
            });

        });
    }

    @Test
    public void annotationsPolygon() throws IOException {

        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/polygon-potrait.pdf"));
        parameters.setPageSize(annotPageSize);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forPdfOutput(d -> {
            d.getPage(0).getAnnotations().forEach(a -> {
                if (a.getSubtype().equals("Polygon")) {
                    assertEqualsRect(new PDRectangle(431, 1218, 391, 351), a.getRectangle());
                    assertArrayEquals(
                            new float[] { 454, 1503, 607, 1567, 799, 1523, 820, 1339, 576, 1221, 433, 1294, 454, 1503 },
                            a.getCOSObject().getDictionaryObject(COSName.VERTICES, COSArray.class).toFloatArray(), 1);
                }
            });

        });
    }

    @Test
    public void annotationsLine() throws IOException {

        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/line-potrait_by_rotation.pdf"));
        parameters.setPageSize(annotPageSize);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forPdfOutput(d -> {
            d.getPage(0).getAnnotations().forEach(a -> {
                if (a.getSubtype().equals("Line")) {
                    assertEqualsRect(new PDRectangle(1584, 884, 49, 220), a.getRectangle());
                    assertArrayEquals(new float[] { 1600, 1092, 1609, 897 },
                            a.getCOSObject().getDictionaryObject(COSName.L, COSArray.class).toFloatArray(), 1);
                }
            });

        });
    }

    @Test
    public void resize_MultipleSizedPages_MixingLandscapePortrait() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/multiple-sized-pages.pdf"));
        parameters.setPageSize(PageSize.A5);
        parameters.addPageRange(new PageRange(1, 2));

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        // number of pages does not change
        testContext.assertPages(3).forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);

            // page has new size
            // was A3 landscape, expect A5 landscape
            PDRectangle expected = PDRectangle.A5.rotate(90);
            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());

            page = d.getPage(1);
            // page has new size
            // was A4 portrait, expect A5 portrait
            expected = PDRectangle.A5;
            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());

            page = d.getPage(2);

            // page has unchanged size
            expected = new PDRectangle(0f, 0f, 841f, 1190f);
            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());
        });
    }

    @Test
    public void resize_allLandscape() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/multiple-sized-pages-all-landscape.pdf"));
        parameters.setPageSize(PageSize.A5);

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        // number of pages does not change
        testContext.assertPages(2).forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);

            // page has new size
            // was A4 landscape, expect A5 landscape
            PDRectangle expected = PDRectangle.A5.rotate(90);
            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());

            page = d.getPage(1);
            // page has new size
            // was A3 landscape, expect A5 landscape
            expected = PDRectangle.A5.rotate(90);
            assertEqualsRect(expected, page.getMediaBox());
            assertEqualsRect(expected, page.getCropBox());
        });
    }

    @Test
    public void noPageSelection() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/test-pdf.pdf"));
        parameters.setPageSize(PageSize.A5);

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        testContext.forEachPdfOutput(d -> {
            // all pages have new size of A5
            PDRectangle expected = PDRectangle.A5;
            for (PDPage page : d.getPages()) {
                assertEqualsRect(expected.rotate(page.getRotation()), page.getMediaBox());
                assertEqualsRect(expected.rotate(page.getRotation()), page.getCropBox());
            }
        });
    }

    @Test
    public void noChanges() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/test-pdf.pdf"));

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        testContext.forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);
            assertEqualsRect(PDRectangle.A4, page.getMediaBox());
            assertEqualsRect(PDRectangle.A4, page.getCropBox());
        });
    }

    private String extractText(PDPage page, Rectangle rect) {
        try {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.addRegion("1", rect);
            stripper.extractRegions(page);
            return stripper.getTextForRegion("1").replaceAll("\\n", "").trim();
        } catch (IOException e) {
            return null;
        }
    }

    private void assertEqualsRect(PDRectangle r1, PDRectangle r2) {
        float delta = 2.0f;
        assertEquals("lowerLeftX", r1.getLowerLeftX(), r2.getLowerLeftX(), delta);
        assertEquals("lowerLeftY", r1.getLowerLeftY(), r2.getLowerLeftY(), delta);
        assertEquals("height", r1.getHeight(), r2.getHeight(), delta);
        assertEquals("width", r1.getWidth(), r2.getWidth(), delta);
    }
}
