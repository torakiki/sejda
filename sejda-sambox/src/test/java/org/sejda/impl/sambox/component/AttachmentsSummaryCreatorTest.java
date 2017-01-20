/*
 * Created on 20 jan 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;

public class AttachmentsSummaryCreatorTest {
    @Test
    public void testHasToc() {
        AttachmentsSummaryCreator victim = new AttachmentsSummaryCreator(new PDDocument());
        assertFalse(victim.hasToc());
        victim.appendItem("text", new PDAnnotationFileAttachment());
        assertTrue(victim.hasToc());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidString() {
        new AttachmentsSummaryCreator(new PDDocument()).appendItem(" ", new PDAnnotationFileAttachment());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendItemInvalidAnnotation() {
        new AttachmentsSummaryCreator(new PDDocument()).appendItem("Text", null);
    }

    @Test
    public void testAddToC() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        AttachmentsSummaryCreator victim = new AttachmentsSummaryCreator(doc);
        victim.appendItem("text", new PDAnnotationFileAttachment());
        victim.addToC();
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void testAddToCTop() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        AttachmentsSummaryCreator victim = new AttachmentsSummaryCreator(doc);
        victim.appendItem("text", new PDAnnotationFileAttachment());
        victim.addToC();
        assertEquals(4, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(1).getCOSObject());
    }

    @Test
    public void testAddTwoPagesToC() throws IOException {
        PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf")));
        PDPage firstPage = doc.getPage(0);
        assertEquals(3, doc.getNumberOfPages());
        AttachmentsSummaryCreator victim = new AttachmentsSummaryCreator(doc);
        for (int i = 1; i < 40; i++) {
            victim.appendItem("text", new PDAnnotationFileAttachment());
        }
        victim.addToC();
        assertEquals(5, doc.getNumberOfPages());
        assertEquals(firstPage.getCOSObject(), doc.getPage(2).getCOSObject());
    }

    @Test
    public void testAddToCSuperLongText() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        AttachmentsSummaryCreator victim = new AttachmentsSummaryCreator(doc);
        victim.appendItem(
                "This is a very long file name and we expect that it's handled correctly and no Exception is thrown by the component. We are making this very very long so we can make sure that even the veeeery long ones are handled.",
                new PDAnnotationFileAttachment());
        victim.addToC();
        assertEquals(1, doc.getNumberOfPages());
    }

    @Test
    public void testStringsThatMixMultipleFontRequirements() {
        PDDocument doc = new PDDocument();
        assertEquals(0, doc.getNumberOfPages());
        AttachmentsSummaryCreator victim = new AttachmentsSummaryCreator(doc);
        victim.appendItem("1-abc-עברית", new PDAnnotationFileAttachment());
        victim.addToC();
        assertEquals(1, doc.getNumberOfPages());
    }
}
