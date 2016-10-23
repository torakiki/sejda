/* 
 * This file is part of the Sejda source code
 * Created on 11/mar/2015
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 * @author Andrea Vacondio
 *
 */
public class SamboxOutlineLevelsHandlerTest {

    private PDDocument fromResource(String name) throws IOException {
        return PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream(name)));
    }


    @Test
    public void getPageNumbersAtOutlineLevel() throws IOException {
        try (PDDocument document = fromResource("pdf/test_outline.pdf")) {
            org.sejda.model.outline.OutlineLevelsHandler victim = new SamboxOutlineLevelsHandler(document, null);
            assertTrue(victim.getPageDestinationsForLevel(4).getPages().isEmpty());
            assertEquals(2, victim.getPageDestinationsForLevel(2).getPages().size());
            assertEquals(1, victim.getPageDestinationsForLevel(3).getPages().size());
        }
    }

    @Test
    public void getPageNumbersAtOutlineLevelNoOutline() throws IOException {
        try (PDDocument document = fromResource("pdf/test_no_outline.pdf")) {
            org.sejda.model.outline.OutlineLevelsHandler victim = new SamboxOutlineLevelsHandler(document, null);
            assertEquals(0, victim.getPageDestinationsForLevel(2).getPages().size());
        }
    }

    @Test
    public void getPageNumbersAtOutlineLevelMatching() throws IOException {
        try (PDDocument document = fromResource("pdf/test_outline.pdf")) {
            org.sejda.model.outline.OutlineLevelsHandler victim = new SamboxOutlineLevelsHandler(document, "(.+)page(.*)");
            assertEquals(1, victim.getPageDestinationsForLevel(2).getPages().size());
        }
    }

    @Test
    public void parentsNoPageDests() {
        PDPage page1 = new PDPage();
        PDDocument document = new PDDocument();
        document.addPage(page1);
        PDDocumentOutline outlines = new PDDocumentOutline();
        PDOutlineItem root = new PDOutlineItem();
        root.setTitle("title");
        PDOutlineItem child = new PDOutlineItem();
        child.setTitle("child");
        PDOutlineItem child2 = new PDOutlineItem();
        child2.setTitle("child2");
        child2.setDestination(page1);
        child.addFirst(child2);
        root.addLast(child);
        outlines.addFirst(root);
        document.getDocumentCatalog().setDocumentOutline(outlines);
        org.sejda.model.outline.OutlineLevelsHandler victim = new SamboxOutlineLevelsHandler(document, null);
        assertEquals(0, victim.getPageDestinationsForLevel(2).getPages().size());
        assertEquals(1, victim.getPageDestinationsForLevel(3).getPages().size());
    }
}
