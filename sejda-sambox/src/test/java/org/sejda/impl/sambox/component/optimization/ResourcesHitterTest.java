/*
 * Created on 01 feb 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.impl.sambox.component.optimization;

import org.junit.jupiter.api.Test;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceDictionary;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourcesHitterTest {

    private ResourcesHitter victim = new ResourcesHitter();

    @Test
    public void testAccept() throws Exception {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/draw_w_transparency.pdf")))) {
            document.getPages().forEach(victim);
            PDPage page = document.getPage(0);
            COSDictionary pageRes = page.getResources().getCOSObject();
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    pageRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("x5")).getCOSObject());
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    pageRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("x7")).getCOSObject());
            PDFormXObject form = (PDFormXObject) page.getResources().getXObject(COSName.getPDFName("x7"));
            COSDictionary formRes = form.getResources().getCOSObject();
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    formRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("x10")).getCOSObject(),
                    "Hitter should discover forms nested in form xobjects");
            PDFormXObject nestedForm = (PDFormXObject) form.getResources().getXObject(COSName.getPDFName("x10"));
            COSDictionary nestedFormRes = nestedForm.getResources().getCOSObject();
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    nestedFormRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("x17")).getCOSObject(),
                    "Hitter should discover images nested in form xobjects");
        }
    }

    @Test
    public void anotationsAppearanceIsProcessed() throws Exception {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/popup_annotation.pdf")))) {
            document.getPages().forEach(victim::accept);
            PDPage page = document.getPage(0);
            List<PDAppearanceDictionary> appearence = page.getAnnotations().stream()
                    .filter(a -> a.getSubtype().equals("Text")).map(PDAnnotation::getAppearance).toList();
            assertEquals(1, appearence.size());
            COSDictionary normalAppRes = appearence.get(0).getNormalAppearance().getAppearanceStream().getResources()
                    .getCOSObject();
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    normalAppRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("X0")).getCOSObject(),
                    "Hitter should discover images in appearance streams resource dictionarlies");
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    normalAppRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("X1")).getCOSObject(),
                    "Hitter should discover images in appearance streams resource dictionarlies");

            COSDictionary downAppRes = appearence.get(0).getDownAppearance().getAppearanceStream().getResources()
                    .getCOSObject();
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    downAppRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("X0")).getCOSObject(),
                    "Hitter should discover images in appearance streams resource dictionarlies");
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    downAppRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("X1")).getCOSObject(),
                    "Hitter should discover images in appearance streams resource dictionarlies");
        }
    }

    @Test
    public void testType3() throws Exception {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/type3.pdf")))) {
            document.getPages().forEach(victim);
            PDPage page = document.getPage(0);
            COSDictionary pageRes = page.getResources().getCOSObject();
            assertInstanceOf(InUseDictionary.class, pageRes.getDictionaryObject(COSName.FONT, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("A")).getCOSObject());
        }
    }

    @Test
    public void testTilingPattern() throws Exception {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/pattern_shared_res.pdf")))) {
            document.getPages().forEach(victim);
            PDPage page = document.getPage(0);
            COSDictionary pageRes = page.getResources().getCOSObject();
            assertInstanceOf(ReadOnlyFilteredCOSStream.class,
                    pageRes.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("Im1")).getCOSObject());
        }
    }

    @Test
    public void exceptionDoesntBubble() {
        PDPage page = mock(PDPage.class);
        when(page.getCropBox()).thenReturn(new PDRectangle(2f, 2f));
        doThrow(RuntimeException.class).when(page).getResources();
        victim.accept(page);
    }

    @Test
    public void sameFontSameInUseFontInstance() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/multiple_res_dic_sharing_same_font.pdf")))) {
            document.getPages().forEach(victim);
            PDPage page0 = document.getPage(0);
            InUseDictionary page0Font = page0.getResources().getCOSObject()
                    .getDictionaryObject(COSName.FONT, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("F1"), InUseDictionary.class);
            PDPage page1 = document.getPage(1);
            InUseDictionary page1Font = page1.getResources().getCOSObject()
                    .getDictionaryObject(COSName.FONT, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("F1"), InUseDictionary.class);
            assertEquals(page0Font, page1Font);
        }
    }

    @Test
    public void unusedExtgstateAreNotHit() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_pages_res_unused_extgstate.pdf")))) {
            document.getPages().forEach(victim);
            PDPage page0 = document.getPage(0);

            assertNotNull(
                    page0.getResources().getCOSObject().getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("gs2"), InUseDictionary.class),
                    "Hitter should hit used extgstate");

            assertNull(page0.getResources().getCOSObject().getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class)
                            .getDictionaryObject(COSName.getPDFName("gs1"), InUseDictionary.class),
                    "Hitter should not hit unused extgstate");

        }
    }

}
