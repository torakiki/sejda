/*
 * Created on 06 set 2016
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

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageLayout;
import org.sejda.sambox.pdmodel.PageMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDThreadBead;

import static org.junit.Assert.*;

/**
 * @author Andrea Vacondio
 *
 */
public class PDDocumentHandlerTest {

    @Test
    public void discardBeads() throws IOException {
        try (PDDocument document = testDoc("pdf/one_page.pdf")) {
            PDPage page = document.getPage(0);
            page.setThreadBeads(Arrays.asList(new PDThreadBead()));
            assertFalse(page.getThreadBeads().isEmpty());
            PDPage copy = new PDDocumentHandler().importPage(page);
            assertEquals(page.getMediaBox(), copy.getMediaBox());
            assertEquals(page.getCropBox(), copy.getCropBox());
            assertEquals(page.getRotation(), copy.getRotation());
            assertEquals(page.getResources(), copy.getResources());
            assertTrue(copy.getThreadBeads().isEmpty());
        }
    }

    @Test
    public void testAddBlankPageIfOdd() throws IOException {
        try (PDDocumentHandler handler = new PDDocumentHandler()) {
            assertNull(handler.addBlankPageIfOdd(new PDRectangle(10, 10)));
            handler.addBlankPage(new PDRectangle(10, 10));
            assertNotNull(handler.addBlankPageIfOdd(new PDRectangle(10, 10)));
        }
    }

    @Test
    public void layoutAndMode() {
        PDDocumentHandler victim = new PDDocumentHandler();
        victim.initialiseBasedOn(new PDDocument());
        assertFalse(
                victim.getUnderlyingPDDocument().getDocumentCatalog().getCOSObject().containsKey(COSName.PAGE_LAYOUT));
        assertFalse(
                victim.getUnderlyingPDDocument().getDocumentCatalog().getCOSObject().containsKey(COSName.PAGE_MODE));
    }

    @Test
    public void layoutAndModeSet() {
        PDDocument doc = new PDDocument();
        doc.getDocumentCatalog().setPageLayout(PageLayout.TWO_COLUMN_RIGHT);
        doc.getDocumentCatalog().setPageMode(PageMode.USE_OUTLINES);
        PDDocumentHandler victim = new PDDocumentHandler();
        victim.initialiseBasedOn(doc);
        assertEquals(PageLayout.TWO_COLUMN_RIGHT,
                victim.getUnderlyingPDDocument().getDocumentCatalog().getPageLayout());
        assertEquals(PageMode.USE_OUTLINES, victim.getUnderlyingPDDocument().getDocumentCatalog().getPageMode());
    }

    @Test
    public void inheritedMediaBox() throws IOException {
        try (PDDocumentHandler handler1 = new PDDocumentHandler(testDoc("pdf/media_box_inherited.pdf"))) {
            PDPage page = handler1.getPage(1);
            assertEquals(PDRectangle.A4, page.getMediaBox());

            PDDocumentHandler handler2 = new PDDocumentHandler();
            PDPage imported = handler2.importPage(page);

            assertEquals("Imported page has different media box than source", imported.getMediaBox(), page.getMediaBox());
        }
    }

    private PDDocument testDoc(String resourceName) throws IOException {
        return PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream(resourceName)));
    }
}
