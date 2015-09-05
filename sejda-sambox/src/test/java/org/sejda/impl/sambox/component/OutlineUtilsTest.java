/*
 * Created on 16/ago/2015
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 * @author Andrea Vacondio
 *
 */
public class OutlineUtilsTest {
    @Test
    public void outlineMaxDepth() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(getClass()
                .getResourceAsStream("/pdf/test_outline.pdf")))) {
            assertEquals(3, OutlineUtils.getMaxOutlineLevel(doc));
        }
    }

    @Test
    public void noOutlineMaxDepth() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(getClass()
                .getResourceAsStream("/pdf/test_no_outline.pdf")))) {
            assertEquals(0, OutlineUtils.getMaxOutlineLevel(doc));
        }
    }

    @Test
    public void toPageDestinationEmpty() {
        PDOutlineItem victim = new PDOutlineItem();
        assertFalse(OutlineUtils.toPageDestination(victim, null).isPresent());
    }

    @Test
    public void toPageDestinationAction() {
        PDPageFitDestination destination = new PDPageFitDestination();
        PDPage page = new PDPage();
        destination.setPage(page);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(destination);
        PDOutlineItem victim = new PDOutlineItem();
        victim.setAction(action);
        assertEquals(destination.getPage(), OutlineUtils.toPageDestination(victim, null).get().getPage());
    }

    @Test
    public void toPageDestinationDestination() {
        PDPageFitDestination destination = new PDPageFitDestination();
        destination.setPageNumber(5);
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        assertEquals(5, OutlineUtils.toPageDestination(victim, catalog).get().getPageNumber());
    }

    @Test
    public void toPageDestinationDestinationNullCatalog() {
        PDPageFitDestination dest = new PDPageFitDestination();
        dest.setPageNumber(5);
        PDNamedDestination destination = new PDNamedDestination();
        destination.setNamedDestination("ChuckNorris");
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        PDDocumentCatalog catalog = null;
        assertFalse(OutlineUtils.toPageDestination(victim, catalog).isPresent());
    }

    @Test
    public void toPageDestinationNamedDestinationNullNames() {
        PDNamedDestination destination = new PDNamedDestination();
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        assertFalse(OutlineUtils.toPageDestination(victim, null).isPresent());
    }

    @Test
    public void toPageDestinationNamedDestination() throws IOException {
        PDPageFitDestination dest = new PDPageFitDestination();
        dest.setPageNumber(5);
        PDNamedDestination destination = new PDNamedDestination();
        destination.setNamedDestination("ChuckNorris");
        PDOutlineItem victim = new PDOutlineItem();
        victim.setDestination(destination);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        when(catalog.findNamedDestinationPage(any())).thenReturn(dest);
        assertEquals(dest, OutlineUtils.toPageDestination(victim, catalog).get());
    }

    @Test
    public void copyDictionary() {
        PDOutlineItem from = new PDOutlineItem();
        from.setBold(true);
        from.setItalic(true);
        from.setTitle("Chuck");
        PDOutlineItem to = new PDOutlineItem();
        to.setBold(false);
        to.setItalic(false);
        OutlineUtils.copyOutlineDictionary(from, to);
        assertTrue(to.isBold());
        assertTrue(to.isItalic());
        assertEquals("Chuck", to.getTitle());
    }
}
