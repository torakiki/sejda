/*
 * Created on 27 giu 2016
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
package org.sejda.impl.sambox.component.split;

import org.junit.jupiter.api.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDThreadBead;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 *
 */
public class PageCopierTest {

    @Test
    public void existingPage() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_resource_dic_w_images.pdf")))) {
            PDPage page = document.getPage(0);
            PDPage copy = new PageCopier(false).copyOf(page);
            PDPage optimizedCopy = new PageCopier(true).copyOf(page);
            assertEquals(page.getMediaBox(), copy.getMediaBox());
            assertEquals(page.getCropBox(), copy.getCropBox());
            assertEquals(page.getRotation(), copy.getRotation());
            assertEquals(page.getResources(), copy.getResources());
            assertNotEquals(page.getResources(), optimizedCopy.getResources());
        }
    }

    @Test
    public void discardBeads() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_resource_dic_w_images.pdf")))) {
            PDPage page = document.getPage(0);
            page.setThreadBeads(List.of(new PDThreadBead()));
            assertFalse(page.getThreadBeads().isEmpty());
            PDPage copy = new PageCopier(false).copyOf(page);
            assertTrue(copy.getThreadBeads().isEmpty());
        }
    }

    @Test
    public void removesParentAndPopup() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/popup_annotation.pdf")))) {
            PDPage page = document.getPage(0);
            PDPage copy = new PageCopier(false).copyOf(page);
            copy.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(d -> {
                assertFalse(d.containsKey(COSName.PARENT));
                assertFalse(d.containsKey(COSName.getPDFName("Popup")));
            });
        }
    }

    @Test
    public void pageWithAnnots() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_full_dic.pdf")))) {
            PDPage page = document.getPage(0);
            PDPage copy = new PageCopier(false).copyOf(page);
            assertEquals(page.getCOSObject().getDictionaryObject(COSName.ANNOTS),
                    page.getCOSObject().getDictionaryObject(COSName.ANNOTS));
            assertNotEquals(page.getCOSObject().getDictionaryObject(COSName.ANNOTS),
                    copy.getCOSObject().getDictionaryObject(COSName.ANNOTS));
            copy.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(d -> {
                assertFalse(d.containsKey(COSName.P));
                assertFalse(d.containsKey(COSName.DEST));
            });
        }
    }

    @Test
    public void removeActionWithDestination() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_full_dic.pdf")))) {
            PDPage page = document.getPage(0);
            PDAnnotationLink link = new PDAnnotationLink();
            PDActionGoTo action = new PDActionGoTo();
            action.setDestination(PDDestination.create(COSName.C));
            link.setAction(action);
            page.setAnnotations(List.of(link));
            PDPage copy = new PageCopier(false).copyOf(page);
            assertEquals(page.getCOSObject().getDictionaryObject(COSName.ANNOTS),
                    page.getCOSObject().getDictionaryObject(COSName.ANNOTS));
            assertNotEquals(page.getCOSObject().getDictionaryObject(COSName.ANNOTS),
                    copy.getCOSObject().getDictionaryObject(COSName.ANNOTS));
            copy.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(d -> {
                assertFalse(d.containsKey(COSName.P));
                assertFalse(d.containsKey(COSName.DEST));
                assertFalse(d.containsKey(COSName.A));
            });
        }
    }

    @Test
    public void doesntRemoveActionWithoutDestination() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_full_dic.pdf")))) {
            PDPage page = document.getPage(0);
            PDAnnotationLink link = new PDAnnotationLink();
            PDActionGoTo action = new PDActionGoTo();
            link.setAction(action);
            page.setAnnotations(List.of(link));
            PDPage copy = new PageCopier(false).copyOf(page);
            assertEquals(page.getCOSObject().getDictionaryObject(COSName.ANNOTS),
                    page.getCOSObject().getDictionaryObject(COSName.ANNOTS));
            assertNotEquals(page.getCOSObject().getDictionaryObject(COSName.ANNOTS),
                    copy.getCOSObject().getDictionaryObject(COSName.ANNOTS));
            copy.getAnnotations().stream().map(PDAnnotation::getCOSObject).forEach(d -> {
                assertFalse(d.containsKey(COSName.P));
                assertFalse(d.containsKey(COSName.DEST));
                assertTrue(d.containsKey(COSName.A));
            });
        }
    }

    @Test
    public void copyStreamIsSanitized() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/annots_in_stream.pdf")))) {
            PDPage page = document.getPage(0);
            assertNotNull(
                    page.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSStream.class).getItem(COSName.ANNOTS));
            PDPage copy = new PageCopier(false).copyOf(page);
            assertNull(
                    copy.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSStream.class).getItem(COSName.ANNOTS));
        }
    }

    @Test
    public void copyStreamInArrayIsSanitized() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/annots_in_contents_array_stream.pdf")))) {
            PDPage page = document.getPage(0);
            assertNotNull(page.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSArray.class)
                    .getObject(0, COSStream.class).getItem(COSName.ANNOTS));
            PDPage copy = new PageCopier(false).copyOf(page);
            assertNull(copy.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSArray.class)
                    .getObject(0, COSStream.class).getItem(COSName.ANNOTS));
        }
    }

}
