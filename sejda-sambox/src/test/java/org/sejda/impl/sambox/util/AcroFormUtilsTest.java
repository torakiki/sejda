/*
 * Created on 16 set 2016
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
package org.sejda.impl.sambox.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;

/**
 * @author Andrea Vacondio
 *
 */
public class AcroFormUtilsTest {

    @Test
    public void megeDetaults() throws IOException {
        try (PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_full_dic.pdf")))) {
            PDAcroForm destination = new PDAcroForm(new PDDocument());
            AcroFormUtils.mergeDefaults(anotherDoc.getDocumentCatalog().getAcroForm(), destination);
            assertEquals(2, destination.getQuadding());
            assertEquals("/ArialMT 0 Tf 0 g ", destination.getDefaultAppearance());
            assertTrue(destination.isNeedAppearances());
            assertTrue(destination.getDefaultResources().getCOSObject().size() > 0);
        }
    }

    @Test
    public void falseNeedAppearance() {
        PDAcroForm mergeThis = new PDAcroForm(new PDDocument());
        mergeThis.setNeedAppearances(false);
        PDAcroForm intoThis = new PDAcroForm(new PDDocument());
        intoThis.setNeedAppearances(true);
        AcroFormUtils.mergeDefaults(mergeThis, intoThis);
        assertTrue(intoThis.isNeedAppearances());
    }

    @Test
    public void blankNeedAppearance() {
        PDAcroForm mergeThis = new PDAcroForm(new PDDocument());
        mergeThis.setDefaultAppearance("this");
        PDAcroForm intoThis = new PDAcroForm(new PDDocument());
        intoThis.setDefaultAppearance("that");
        AcroFormUtils.mergeDefaults(mergeThis, intoThis);
        assertEquals("that", intoThis.getDefaultAppearance());
    }

    @Test
    public void negativeQuadding() {
        PDAcroForm mergeThis = new PDAcroForm(new PDDocument());
        mergeThis.setQuadding(-2);
        PDAcroForm intoThis = new PDAcroForm(new PDDocument());
        AcroFormUtils.mergeDefaults(mergeThis, intoThis);
        assertEquals(0, intoThis.getQuadding());
    }

    @Test
    public void invalidQuadding() {
        PDAcroForm mergeThis = new PDAcroForm(new PDDocument());
        mergeThis.setQuadding(20);
        PDAcroForm intoThis = new PDAcroForm(new PDDocument());
        AcroFormUtils.mergeDefaults(mergeThis, intoThis);
        assertEquals(0, intoThis.getQuadding());
    }

    @Test
    public void alreadySetQuadding() {
        PDAcroForm mergeThis = new PDAcroForm(new PDDocument());
        mergeThis.setQuadding(2);
        PDAcroForm intoThis = new PDAcroForm(new PDDocument());
        intoThis.setQuadding(1);
        AcroFormUtils.mergeDefaults(mergeThis, intoThis);
        assertEquals(1, intoThis.getQuadding());
    }

    @Test
    public void invalidResourceType() {
        PDAcroForm mergeThis = new PDAcroForm(new PDDocument());
        PDResources dr = new PDResources();
        dr.getCOSObject().setInt(COSName.FONT, 10);
        mergeThis.setDefaultResources(dr);
        PDAcroForm intoThis = new PDAcroForm(new PDDocument());
        AcroFormUtils.mergeDefaults(mergeThis, intoThis);
        assertNull(intoThis.getDefaultResources().getCOSObject().getItem(COSName.FONT));
    }

    @Test
    public void mergeFormsWithProcSet() throws IOException {

        try (PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_proc_set.pdf")))) {
            PDAcroForm destination = new PDAcroForm(new PDDocument());
            AcroFormUtils.mergeDefaults(anotherDoc.getDocumentCatalog().getAcroForm(), destination);
            COSBase procSet = destination.getDefaultResources().getCOSObject().getItem(COSName.PROC_SET);
            assertNotNull(procSet);
            assertEquals(2, ((COSArray) procSet).size());
        }

    }
}
