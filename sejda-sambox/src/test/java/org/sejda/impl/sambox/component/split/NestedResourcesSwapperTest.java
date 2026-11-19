/*
 * Created on 18/11/26
 * Copyright 2026 Sober Lemur S.r.l. and Sejda BV
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
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Andrea Vacondio
 */
public class NestedResourcesSwapperTest {

    @Test
    // form resources are the same resource dictionary of the pages
    public void swapsFormResources() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_res_dic_page_and_form.pdf")))) {
            COSDictionary pageResources = document.getPage(0).getResources().getCOSObject();
            COSStream form = pageResources.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("Fm"), COSStream.class);
            COSBase formResources = form.getItem(COSName.RESOURCES);
            try (NestedResourcesSwapper swapper = new NestedResourcesSwapper()) {
                swapper.swap(document.getPage(0));
                COSDictionary copy = form.getDictionaryObject(COSName.RESOURCES, COSDictionary.class);
                assertNotSame(pageResources, copy);
                assertNotSame(pageResources.getDictionaryObject(COSName.FONT), copy.getDictionaryObject(COSName.FONT));
                assertNotSame(pageResources.getDictionaryObject(COSName.EXT_G_STATE), copy.getDictionaryObject(COSName.EXT_G_STATE));
                assertNotSame(pageResources.getDictionaryObject(COSName.XOBJECT), copy.getDictionaryObject(COSName.XOBJECT));
                assertNotNull(copy.getDictionaryObject(COSName.FONT, COSDictionary.class).getItem(COSName.getPDFName("FN")));
                assertNotNull(copy.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class).getItem(COSName.getPDFName("GSN")));
                assertNotNull(copy.getDictionaryObject(COSName.XOBJECT, COSDictionary.class).getItem(COSName.getPDFName("ImN")));
                assertSame(pageResources, document.getPage(0).getResources().getCOSObject());
            }
            assertSame(formResources, form.getItem(COSName.RESOURCES));
        }
    }

    @Test
    // soft mask group resources have the same Font, ExtGState and XObject dictionaries of the pages
    public void swapsSoftMaskGroupResources() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_name_dics_page_and_softmask.pdf")))) {
            COSDictionary pageResources = document.getPage(0).getResources().getCOSObject();
            COSDictionary group = pageResources.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("GSM"), COSDictionary.class)
                    .getDictionaryObject(COSName.SMASK, COSDictionary.class)
                    .getDictionaryObject(COSName.G, COSDictionary.class);
            COSBase groupResources = group.getItem(COSName.RESOURCES);
            try (NestedResourcesSwapper swapper = new NestedResourcesSwapper()) {
                swapper.swap(document.getPage(0));
                COSDictionary copy = group.getDictionaryObject(COSName.RESOURCES, COSDictionary.class);
                assertNotSame(groupResources, copy);
                assertNotSame(pageResources.getDictionaryObject(COSName.FONT), copy.getDictionaryObject(COSName.FONT));
                assertNotSame(pageResources.getDictionaryObject(COSName.EXT_G_STATE), copy.getDictionaryObject(COSName.EXT_G_STATE));
                assertNotSame(pageResources.getDictionaryObject(COSName.XOBJECT), copy.getDictionaryObject(COSName.XOBJECT));
                assertNotNull(copy.getDictionaryObject(COSName.FONT, COSDictionary.class).getItem(COSName.getPDFName("FN")));
                assertNotNull(copy.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class).getItem(COSName.getPDFName("GSN")));
                assertNotNull(copy.getDictionaryObject(COSName.XOBJECT, COSDictionary.class).getItem(COSName.getPDFName("ImN")));
            }
            assertSame(groupResources, group.getItem(COSName.RESOURCES));
        }
    }

    @Test
    // form Fm draws form Fm2, both forms have the resource dictionary of the pages
    public void formsSharingResourcesShareTheCopy() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_res_dic_page_and_nested_forms_every_page.pdf")))) {
            COSDictionary pageResources = document.getPage(0).getResources().getCOSObject();
            COSDictionary xObjects = pageResources.getDictionaryObject(COSName.XOBJECT, COSDictionary.class);
            COSStream form = xObjects.getDictionaryObject(COSName.getPDFName("Fm"), COSStream.class);
            COSStream nestedForm = xObjects.getDictionaryObject(COSName.getPDFName("Fm2"), COSStream.class);
            COSBase formResources = form.getItem(COSName.RESOURCES);
            COSBase nestedFormResources = nestedForm.getItem(COSName.RESOURCES);
            try (NestedResourcesSwapper swapper = new NestedResourcesSwapper()) {
                swapper.swap(document.getPage(0));
                assertNotSame(pageResources, form.getDictionaryObject(COSName.RESOURCES));
                assertSame(form.getDictionaryObject(COSName.RESOURCES), nestedForm.getDictionaryObject(COSName.RESOURCES));
            }
            assertSame(formResources, form.getItem(COSName.RESOURCES));
            assertSame(nestedFormResources, nestedForm.getItem(COSName.RESOURCES));
        }
    }

    @Test
    // the same form is FmA and FmB in the page XObject dictionary and the soft mask group of GS1
    public void restoresFormFoundMoreThanOnce() {
        COSDictionary formResources = new COSDictionary();
        COSStream form = new COSStream();
        form.setItem(COSName.SUBTYPE, COSName.FORM);
        form.setItem(COSName.RESOURCES, formResources);
        COSDictionary xObjects = new COSDictionary();
        xObjects.setItem(COSName.getPDFName("FmA"), form);
        xObjects.setItem(COSName.getPDFName("FmB"), form);
        COSDictionary softMask = new COSDictionary();
        softMask.setItem(COSName.G, form);
        COSDictionary gs = new COSDictionary();
        gs.setItem(COSName.SMASK, softMask);
        COSDictionary extGStates = new COSDictionary();
        extGStates.setItem(COSName.getPDFName("GS1"), gs);
        COSDictionary resources = new COSDictionary();
        resources.setItem(COSName.XOBJECT, xObjects);
        resources.setItem(COSName.EXT_G_STATE, extGStates);
        PDPage page = new PDPage();
        page.setResources(new PDResources(resources));
        try (NestedResourcesSwapper swapper = new NestedResourcesSwapper()) {
            swapper.swap(page);
            assertNotSame(formResources, form.getItem(COSName.RESOURCES));
        }
        assertSame(formResources, form.getItem(COSName.RESOURCES));
    }

}
