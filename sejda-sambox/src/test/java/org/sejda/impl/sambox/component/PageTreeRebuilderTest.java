/*
 * Created on 06 feb 2017
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * @author Andrea Vacondio
 *
 */
public class PageTreeRebuilderTest {

    @Test
    public void getAndRepairParentHasParent() {
        COSDictionary parent = new COSDictionary();
        COSDictionary child = new COSDictionary();
        child.setItem(COSName.PARENT, parent);
        assertEquals(parent, PageTreeRebuilder.getAndRepairParent(child));
    }

    @Test
    public void getAndRepairParentHasParentInWrongPKey() {
        COSDictionary parent = new COSDictionary();
        COSDictionary child = new COSDictionary();
        child.setItem(COSName.P, parent);
        assertEquals(parent, PageTreeRebuilder.getAndRepairParent(child));
        assertEquals(parent, child.getItem(COSName.PARENT));
    }

    @Test
    public void nullGetAndRepairParent() {
        COSDictionary child = new COSDictionary();
        assertNull(PageTreeRebuilder.getAndRepairParent(child));
    }

    @Test
    public void isPagePositive() {
        COSDictionary page = new COSDictionary();
        page.setItem(COSName.TYPE, COSName.PAGE);
        assertTrue(PageTreeRebuilder.isPage(page));
    }

    @Test
    public void isPagePositiveMissingType() {
        COSDictionary parent = new COSDictionary();
        COSDictionary child = new COSDictionary();
        child.setItem(COSName.PARENT, parent);
        assertTrue(PageTreeRebuilder.isPage(child));
    }

    @Test
    public void isPageNegative() {
        COSDictionary page = new COSDictionary();
        page.setItem(COSName.TYPE, COSName.CATALOG);
        assertFalse(PageTreeRebuilder.isPage(page));
    }

    @Test
    public void invalidPageStream() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/invalid_page_stream.pdf")))) {
            assertFalse(PageTreeRebuilder.canDecodeContents(doc.getPage(0).getCOSObject()));
        }
    }

    @Test
    public void validPageStream() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            assertTrue(PageTreeRebuilder.canDecodeContents(doc.getPage(0).getCOSObject()));
        }
    }

    @Test
    public void countCorrected() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/missing_page_ref.pdf")))) {
            new PageTreeRebuilder(doc).rebuild();
            assertEquals(3, doc.getPages().getCount());
        }
    }
}
