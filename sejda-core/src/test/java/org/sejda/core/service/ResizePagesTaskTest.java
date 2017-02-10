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

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.parameter.ResizePagesParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
            assertEqualsR(expected, page.getMediaBox());
            assertEqualsR(expected, page.getCropBox());

            // contents is scaled to create margins
            String content = extractText(page, new Rectangle(115, 165, 332, 35));
            assertEquals("Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>" +
                    " Everyone is permitted to copy and distribute verbatim copies" +
                    " of this license document, but changing it is not allowed.", content);

            page = d.getPage(3);

            assertEqualsR(expected, page.getMediaBox());
            assertEqualsR(expected, page.getCropBox());

            content = extractText(page, new Rectangle(65, 54, 91, 15));
            assertEquals("You may charge", content);
        });
    }

    @Test
    public void resizePages() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/multiple-sized-pages.pdf"));
        parameters.setPageSizeWidth(20);
        parameters.addPageRange(new PageRange(1, 2));

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        // number of pages does not change
        testContext.assertPages(3).forEachPdfOutput(d -> {
            PDPage page = d.getPage(0);

            // page has new size
            // landscape
            PDRectangle expected = new PDRectangle(0f, 0f, 2037.5743f, 1440f);
            assertEqualsR(expected, page.getMediaBox());
            assertEqualsR(expected, page.getCropBox());

            page = d.getPage(1);
            // page has new size
            // portrait
            expected = new PDRectangle(0f, 0f, 1440.0f, 2038.788f);
            assertEqualsR(expected, page.getMediaBox());
            assertEqualsR(expected, page.getCropBox());

            page = d.getPage(2);
            // page has old size
            expected = new PDRectangle(0f, 0f, 841f, 1190f);
            assertEqualsR(expected, page.getMediaBox());
            assertEqualsR(expected, page.getCropBox());
        });
    }

    @Test
    public void noPageSelection() throws IOException {
        ResizePagesParameters parameters = new ResizePagesParameters();
        parameters.addSource(customInput("pdf/test-pdf.pdf"));
        parameters.setPageSizeWidth(20);

        testContext.directoryOutputTo(parameters);

        execute(parameters);

        testContext.assertTaskCompleted();

        testContext.forEachPdfOutput(d -> {
            // all pages have new size
            PDRectangle expected = new PDRectangle(0f, 0f, 2037.5743f, 1440f);
            for (PDPage page : d.getPages()) {
                assertEqualsR(expected.rotate(page.getRotation()), page.getMediaBox());
                assertEqualsR(expected.rotate(page.getRotation()), page.getCropBox());
            }
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

    private void assertEqualsR(PDRectangle r1, PDRectangle r2) {
        assertEquals(r1.getLowerLeftX(), r2.getLowerLeftX(), 1.0f);
    }
}
