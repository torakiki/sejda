/*
 * Created on 18 set 2015
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;
import org.sejda.sambox.pdmodel.interactive.form.PDField;
import org.sejda.sambox.pdmodel.interactive.form.PDFieldFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 *
 */
public class SignatureClipperTest {

    private COSDictionary dictionary;

    @BeforeEach
    public void setUp() {
        dictionary = new COSDictionary();
        dictionary.setItem(COSName.FT, COSName.SIG);
        dictionary.setItem(COSName.V, COSName.getPDFName("value"));
        dictionary.setItem(COSName.SV, COSName.getPDFName("seed"));
        dictionary.setItem(COSName.LOCK, COSName.getPDFName("lock"));
    }

    @Test
    public void nullCollectionDoesntFail() {
        SignatureClipper.clipSignatures((Collection<PDAnnotation>) null);
    }

    @Test
    public void nullFieldDoesntFail() {
        assertFalse(SignatureClipper.clipSignature(null));
    }

    @Test
    public void clipWidget() {
        PDAnnotationWidget widget = new PDAnnotationWidget(dictionary);
        SignatureClipper.clipSignatures(Arrays.asList(widget));
        assertFalse(widget.getCOSObject().containsKey(COSName.V));
        assertFalse(widget.getCOSObject().containsKey(COSName.SV));
        assertFalse(widget.getCOSObject().containsKey(COSName.LOCK));
    }

    @Test
    public void clipNonWidget() {
        PDAnnotationLink widget = new PDAnnotationLink(dictionary);
        SignatureClipper.clipSignatures(Arrays.asList(widget));
        assertTrue(widget.getCOSObject().containsKey(COSName.V));
        assertTrue(widget.getCOSObject().containsKey(COSName.SV));
        assertTrue(widget.getCOSObject().containsKey(COSName.LOCK));
    }

    @Test
    public void clipWidgetNonSign() {
        dictionary.setItem(COSName.FT, COSName.BTN);
        PDAnnotationWidget widget = new PDAnnotationWidget(dictionary);
        SignatureClipper.clipSignatures(Arrays.asList(widget));
        assertTrue(widget.getCOSObject().containsKey(COSName.V));
        assertTrue(widget.getCOSObject().containsKey(COSName.SV));
        assertTrue(widget.getCOSObject().containsKey(COSName.LOCK));
    }

    @Test
    public void clipField() {
        PDField field = PDFieldFactory.createFieldAddingChildToParent(new PDAcroForm(new PDDocument()), dictionary,
                null);
        assertTrue(SignatureClipper.clipSignature(field));
        assertFalse(field.getCOSObject().containsKey(COSName.V));
        assertFalse(field.getCOSObject().containsKey(COSName.SV));
        assertFalse(field.getCOSObject().containsKey(COSName.LOCK));
    }

    @Test
    public void clipFieldNotSignature() {
        dictionary.setItem(COSName.FT, COSName.BTN);
        PDField field = PDFieldFactory.createFieldAddingChildToParent(new PDAcroForm(new PDDocument()), dictionary,
                null);
        SignatureClipper.clipSignature(field);
        assertTrue(field.getCOSObject().containsKey(COSName.V));
        assertTrue(field.getCOSObject().containsKey(COSName.SV));
        assertTrue(field.getCOSObject().containsKey(COSName.LOCK));
    }
}
