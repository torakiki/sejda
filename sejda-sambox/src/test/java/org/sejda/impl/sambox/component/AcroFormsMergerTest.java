/*
 * Created on 17 set 2015
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.common.LookupTable;
import org.sejda.io.SeekableSources;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;
import org.sejda.sambox.pdmodel.interactive.form.PDField;
import org.sejda.sambox.pdmodel.interactive.form.PDRadioButton;
import org.sejda.sambox.pdmodel.interactive.form.PDTerminalField;
import org.sejda.util.IOUtils;

/**
 * @author Andrea Vacondio
 *
 */
public class AcroFormsMergerTest {
    private PDDocument document;
    private LookupTable<PDPage> mapping = new LookupTable<>();
    private LookupTable<PDAnnotation> annotationsLookup;

    @Before
    public void setUp() throws IOException {
        document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf")));
        for (PDPage current : document.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
            annotationsLookup = new AnnotationsDistiller(document).retainRelevantAnnotations(mapping);
        }
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(document);
    }

    @Test
    public void mergeNull() {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(null, annotationsLookup);
        assertFalse(victim.hasForm());
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void mergeNoRelevant() {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        annotationsLookup.clear();
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        assertFalse(victim.hasForm());
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void mergeWithXFA() {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        PDAcroForm form = document.getDocumentCatalog().getAcroForm();
        assertNotNull(form);
        form.getCOSObject().setString(COSName.XFA, "Something");
        victim.mergeForm(form, annotationsLookup);
        assertFalse(victim.hasForm());
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void discard() {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.DISCARD, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        assertFalse(victim.hasForm());
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void merge() {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        assertTrue(victim.hasForm());
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void mergeWithHierarchy() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        mapping.clear();
        annotationsLookup.clear();

        try (PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_hierarchy.pdf")))) {
            mapping.addLookupEntry(anotherDoc.getPage(0), new PDPage());
            annotationsLookup = new AnnotationsDistiller(anotherDoc).retainRelevantAnnotations(mapping);
            victim.mergeForm(anotherDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

            assertTrue(victim.hasForm());
            PDAcroForm form = victim.getForm();
            assertNotNull(form.getField("node.leaf"));
            assertEquals(5, form.getFields().size());
        }
    }

    @Test
    public void mergeRenamingWithHierarchy() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE_RENAMING_EXISTING_FIELDS, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        mapping.clear();
        annotationsLookup.clear();

        try (PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_hierarchy.pdf")))) {
            mapping.addLookupEntry(anotherDoc.getPage(0), new PDPage());
            annotationsLookup = new AnnotationsDistiller(anotherDoc).retainRelevantAnnotations(mapping);
            victim.mergeForm(anotherDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);
            mapping.clear();
            annotationsLookup.clear();
            mapping.addLookupEntry(anotherDoc.getPage(0), new PDPage());
            annotationsLookup = new AnnotationsDistiller(anotherDoc).retainRelevantAnnotations(mapping);
            victim.mergeForm(anotherDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

            assertTrue(victim.hasForm());
            PDAcroForm form = victim.getForm();
            assertNotNull(form.getField("node.leaf"));
            assertEquals(6, form.getFields().size());
        }
    }

    @Test
    public void mergeHalfFormWithAnnotations() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        mapping.clear();
        annotationsLookup.clear();

        try (PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/two_pages_form.pdf")))) {
            mapping.addLookupEntry(anotherDoc.getPage(0), new PDPage());
            annotationsLookup = new AnnotationsDistiller(anotherDoc).retainRelevantAnnotations(mapping);
            victim.mergeForm(anotherDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

            assertTrue(victim.hasForm());
            assertNull(destination.getDocumentCatalog().getAcroForm());
        }
    }

    @Test
    public void mergeOrphans() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        mapping.clear();
        annotationsLookup.clear();

        try (PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/form_orphan_fields.pdf")))) {
            PDPage destPage = new PDPage();
            mapping.addLookupEntry(anotherDoc.getPage(0), destPage);
            annotationsLookup = new AnnotationsDistiller(anotherDoc).retainRelevantAnnotations(mapping);
            victim.mergeForm(anotherDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

            assertTrue(victim.hasForm());
            PDAcroForm form = victim.getForm();
            assertEquals(2, form.getFields().size());
            assertEquals(6, destPage.getAnnotations().size());
        }
    }

    @Test
    public void mergeSame() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        mapping.clear();
        annotationsLookup.clear();

        PDDocument sameDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf")));
        for (PDPage current : sameDoc.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
            annotationsLookup = new AnnotationsDistiller(sameDoc).retainRelevantAnnotations(mapping);
        }
        victim.mergeForm(sameDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

        assertTrue(victim.hasForm());
        assertEquals(4, victim.getForm().getFields().size());
        for (PDField field : victim.getForm().getFieldTree()) {
            if (field.isTerminal()) {
                if (field instanceof PDRadioButton) {
                    assertEquals(4, ((PDTerminalField) field).getWidgets().size());
                } else {
                    assertEquals(2, ((PDTerminalField) field).getWidgets().size());
                }
            }
        }
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void mergeRenaming() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE_RENAMING_EXISTING_FIELDS, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        mapping.clear();
        annotationsLookup.clear();

        PDDocument sameDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf")));
        for (PDPage current : sameDoc.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
            annotationsLookup = new AnnotationsDistiller(sameDoc).retainRelevantAnnotations(mapping);
        }
        victim.mergeForm(sameDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

        assertTrue(victim.hasForm());
        assertEquals(8, victim.getForm().getFields().size());
        for (PDField field : victim.getForm().getFieldTree()) {
            if (field instanceof PDRadioButton) {
                assertEquals(field.getPartialName(), 2, ((PDTerminalField) field).getWidgets().size());
            } else {
                assertEquals(1, ((PDTerminalField) field).getWidgets().size());
            }
        }
        assertNull(destination.getDocumentCatalog().getAcroForm());
    }

    @Test
    public void mergeWithSignatureRemovesSignatureValue() throws IOException {
        PDDocument destination = new PDDocument();
        AcroFormsMerger victim = new AcroFormsMerger(AcroFormPolicy.MERGE, destination);
        assertNotNull(document.getDocumentCatalog().getAcroForm());
        victim.mergeForm(document.getDocumentCatalog().getAcroForm(), annotationsLookup);
        mapping.clear();
        annotationsLookup.clear();

        PDDocument anotherDoc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form_with_signature_signed.pdf")));
        for (PDPage current : anotherDoc.getPages()) {
            mapping.addLookupEntry(current, new PDPage());
            annotationsLookup = new AnnotationsDistiller(anotherDoc).retainRelevantAnnotations(mapping);
        }
        victim.mergeForm(anotherDoc.getDocumentCatalog().getAcroForm(), annotationsLookup);

        assertTrue(victim.hasForm());
        PDAcroForm form = victim.getForm();
        assertNotNull(form);
        PDField signature = null;
        for (PDField current : form.getFieldTree()) {
            if (current.getFieldType() == COSName.SIG.getName()) {
                signature = current;
            }
        }
        assertNotNull(signature);
        assertEquals("", signature.getValueAsString());
        assertTrue(form.isSignaturesExist());
    }

}
