/*
 * Created on 01 feb 2016
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
package org.sejda.impl.sambox.component.optimization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Andrea Vacondio
 *
 */
public class ResourceDictionaryCleanerTest {
    private ReadOnlyFilteredCOSStream imageStream;
    private ReadOnlyFilteredCOSStream formStream;
    private InUseFontDictionary inUseFont;
    private COSStream image;
    private COSStream form;

    @Before
    public void setUp() throws IOException {
        image = new COSStream();
        image.setItem(COSName.TYPE, COSName.XOBJECT);
        image.setItem(COSName.SUBTYPE, COSName.IMAGE);
        form = new COSStream();
        form.setItem(COSName.TYPE, COSName.XOBJECT);
        form.setItem(COSName.SUBTYPE, COSName.FORM);
        imageStream = ReadOnlyFilteredCOSStream.readOnly(image);
        formStream = ReadOnlyFilteredCOSStream.readOnly(form);
        inUseFont = new InUseFontDictionary(new COSDictionary());
    }

    @Test
    public void clean() {
        PDDocument doc = new PDDocument();
        COSDictionary rootRes = new COSDictionary();
        COSDictionary rootXobjects = new COSDictionary();
        rootXobjects.setItem(COSName.getPDFName("keepMe"), imageStream);
        rootXobjects.setItem(COSName.getPDFName("keepMeForm"), formStream);
        rootXobjects.setItem(COSName.getPDFName("discardMe"), image);
        rootXobjects.setItem(COSName.getPDFName("discardMeForm"), form);
        rootRes.setItem(COSName.XOBJECT, rootXobjects);
        COSDictionary rootFonts = new COSDictionary();
        rootFonts.setItem(COSName.getPDFName("keepMe"), inUseFont);
        rootFonts.setItem(COSName.getPDFName("discardMe"), new COSDictionary());
        rootRes.setItem(COSName.FONT, rootFonts);
        COSDictionary pageRes = new COSDictionary();
        COSDictionary pageXobjects = new COSDictionary();
        pageXobjects.setItem(COSName.getPDFName("keepMe"), imageStream);
        pageXobjects.setItem(COSName.getPDFName("keepMeForm"), formStream);
        pageXobjects.setItem(COSName.getPDFName("discardMe"), image);
        pageXobjects.setItem(COSName.getPDFName("discardMeToo"), image);
        pageXobjects.setItem(COSName.getPDFName("discardMeForm"), form);
        pageRes.setItem(COSName.XOBJECT, pageXobjects);
        COSDictionary pageFonts = new COSDictionary();
        pageFonts.setItem(COSName.getPDFName("keepMe"), inUseFont);
        pageFonts.setItem(COSName.getPDFName("discardMe"), new COSDictionary());
        pageFonts.setItem(COSName.getPDFName("discardMeToo"), new COSDictionary());
        pageRes.setItem(COSName.FONT, pageFonts);
        doc.getDocumentCatalog().getPages().getCOSObject().setItem(COSName.RESOURCES, rootRes);
        PDPage page0 = new PDPage();
        PDPage page1 = new PDPage();
        page1.getCOSObject().setItem(COSName.RESOURCES, pageRes);
        doc.getDocumentCatalog().getPages().add(page0);
        doc.getDocumentCatalog().getPages().add(page1);
        assertTrue(page0.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertTrue(page0.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertTrue(page0.getResources().isFormXObject(COSName.getPDFName("keepMeForm")));
        assertTrue(page0.getResources().isFormXObject(COSName.getPDFName("discardMeForm")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertTrue(page1.getResources().isFormXObject(COSName.getPDFName("keepMeForm")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("discardMeToo")));
        assertTrue(page1.getResources().isFormXObject(COSName.getPDFName("discardMeForm")));
        COSDictionary page0fonts = page0.getResources().getCOSObject().getDictionaryObject(COSName.FONT,
                COSDictionary.class);
        assertNotNull(page0fonts.getDictionaryObject(COSName.getPDFName("keepMe")));
        assertNotNull(page0fonts.getDictionaryObject(COSName.getPDFName("discardMe")));
        COSDictionary page1fonts = page1.getResources().getCOSObject().getDictionaryObject(COSName.FONT,
                COSDictionary.class);
        assertNotNull(page1fonts.getDictionaryObject(COSName.getPDFName("keepMe")));
        assertNotNull(page1fonts.getDictionaryObject(COSName.getPDFName("discardMe")));
        assertNotNull(page1fonts.getDictionaryObject(COSName.getPDFName("discardMeToo")));
        new ResourceDictionaryCleaner().accept(doc);
        assertTrue(page0.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertTrue(page0.getResources().isFormXObject(COSName.getPDFName("keepMeForm")));
        assertFalse(page0.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertFalse(page0.getResources().isFormXObject(COSName.getPDFName("discardMeForm")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertTrue(page1.getResources().isFormXObject(COSName.getPDFName("keepMeForm")));
        assertFalse(page1.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertFalse(page1.getResources().isImageXObject(COSName.getPDFName("discardMeToo")));
        assertFalse(page1.getResources().isFormXObject(COSName.getPDFName("discardMeForm")));
        page0fonts = page0.getResources().getCOSObject().getDictionaryObject(COSName.FONT, COSDictionary.class);
        assertNotNull(page0fonts.getDictionaryObject(COSName.getPDFName("keepMe")));
        assertNull(page0fonts.getDictionaryObject(COSName.getPDFName("discardMe")));
        page1fonts = page1.getResources().getCOSObject().getDictionaryObject(COSName.FONT, COSDictionary.class);
        assertNotNull(page1fonts.getDictionaryObject(COSName.getPDFName("keepMe")));
        assertNull(page1fonts.getDictionaryObject(COSName.getPDFName("discardMe")));
        assertNull(page1fonts.getDictionaryObject(COSName.getPDFName("discardMeToo")));
    }

    @Test
    public void noExceptionMissingResources() {
        PDDocument doc = new PDDocument();
        PDPage page0 = new PDPage();
        page0.getCOSObject().setItem(COSName.RESOURCES, null);
        doc.getDocumentCatalog().getPages().add(page0);
        new ResourceDictionaryCleaner().accept(doc);
    }

    @Test
    public void noExceptionMissingXObjects() {
        PDDocument doc = new PDDocument();
        PDPage page0 = new PDPage();
        COSDictionary rootRes = new COSDictionary();
        page0.getCOSObject().setItem(COSName.RESOURCES, rootRes);
        rootRes.setItem(COSName.XOBJECT, null);
        doc.getDocumentCatalog().getPages().add(page0);
        new ResourceDictionaryCleaner().accept(doc);
    }

    @Test
    public void noExceptionMissingFonts() {
        PDDocument doc = new PDDocument();
        PDPage page0 = new PDPage();
        COSDictionary rootRes = new COSDictionary();
        page0.getCOSObject().setItem(COSName.RESOURCES, rootRes);
        rootRes.setItem(COSName.FONT, null);
        doc.getDocumentCatalog().getPages().add(page0);
        new ResourceDictionaryCleaner().accept(doc);
    }
}
