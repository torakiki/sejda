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
package org.sejda.impl.sambox.component.optimizaton;

import static org.junit.Assert.assertFalse;
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
    private ReadOnlyFilteredCOSStream stream;
    private COSStream image;

    @Before
    public void setUp() throws IOException {
        image = new COSStream();
        image.setItem(COSName.TYPE, COSName.XOBJECT);
        image.setItem(COSName.SUBTYPE, COSName.IMAGE);
        stream = ReadOnlyFilteredCOSStream.readOnly(image);
    }

    @Test
    public void clean() {
        PDDocument doc = new PDDocument();
        COSDictionary rootRes = new COSDictionary();
        COSDictionary rootXobjects = new COSDictionary();
        rootXobjects.setItem(COSName.getPDFName("keepMe"), stream);
        rootXobjects.setItem(COSName.getPDFName("discardMe"), image);
        rootRes.setItem(COSName.XOBJECT, rootXobjects);
        COSDictionary pageRes = new COSDictionary();
        COSDictionary pageXobjects = new COSDictionary();
        pageXobjects.setItem(COSName.getPDFName("keepMe"), stream);
        pageXobjects.setItem(COSName.getPDFName("discardMe"), image);
        pageXobjects.setItem(COSName.getPDFName("discardMeToo"), image);
        pageRes.setItem(COSName.XOBJECT, pageXobjects);
        doc.getDocumentCatalog().getPages().getCOSObject().setItem(COSName.RESOURCES, rootRes);
        PDPage page0 = new PDPage();
        PDPage page1 = new PDPage();
        page1.getCOSObject().setItem(COSName.RESOURCES, pageRes);
        doc.getDocumentCatalog().getPages().add(page0);
        doc.getDocumentCatalog().getPages().add(page1);
        assertTrue(page0.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertTrue(page0.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("discardMeToo")));
        new ResourceDictionaryCleaner().accept(doc);
        assertTrue(page0.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertFalse(page0.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertTrue(page1.getResources().isImageXObject(COSName.getPDFName("keepMe")));
        assertFalse(page1.getResources().isImageXObject(COSName.getPDFName("discardMe")));
        assertFalse(page1.getResources().isImageXObject(COSName.getPDFName("discardMeToo")));
    }
}
