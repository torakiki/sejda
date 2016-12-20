/*
 * Created on 04/set/2015
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.sejda.common.LookupTable;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.action.PDActionJavaScript;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationPopup;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationText;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;

/**
 * @author Andrea Vacondio
 *
 */
public class AnnotationsDistillerTest {

    private PDPage oldPage;
    private PDPage newPage;
    private LookupTable<PDPage> lookup;

    @Before
    public void setUp() {
        oldPage = new PDPage();
        newPage = new PDPage();
        lookup = new LookupTable<>();
        lookup.addLookupEntry(oldPage, newPage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fiterNullDocument() {
        new AnnotationsDistiller(null);
    }

    @Test
    public void noLinks() {
        List<PDAnnotation> annotations = Arrays.asList(new PDAnnotationText());
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(1, newPage.getAnnotations().size());
    }

    public void noLinks_PageRelevant() {
        PDPage destPage = new PDPage();
        PDAnnotationText annotation = new PDAnnotationText();
        annotation.setPage(destPage);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation), newPage.getAnnotations().get(0));
    }

    @Test
    public void noLinks_PageNotRelevant() {
        PDAnnotationText annotation = new PDAnnotationText();
        annotation.setPage(new PDPage());
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(0, newPage.getAnnotations().size());
        assertTrue(annotationsLookup.isEmpty());
    }

    @Test
    public void noLinks_OnePageNotRelevantOneRelevant() {
        PDPage destPage = new PDPage();
        PDAnnotationText annotation = new PDAnnotationText();
        annotation.setPage(destPage);
        PDAnnotationText annotation2 = new PDAnnotationText();
        annotation2.setPage(oldPage);
        List<PDAnnotation> annotations = Arrays.asList(annotation, annotation2);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation2), newPage.getAnnotations().get(0));
    }

    @Test
    public void linksNoGoTo() {
        PDAnnotationLink annotation = new PDAnnotationLink();
        annotation.setAction(new PDActionJavaScript());
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation), newPage.getAnnotations().get(0));
    }

    @Test
    public void links_PageNotRelevant() {
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        annotation.setDestination(dest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(0, newPage.getAnnotations().size());
        assertTrue(annotationsLookup.isEmpty());
    }

    @Test
    public void links_PageRelevant() {
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(oldPage);
        annotation.setDestination(dest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation), newPage.getAnnotations().get(0));
    }

    @Test
    public void links_OnePageNotRelevantOneRelevant() {

        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        annotation.setDestination(dest);

        PDAnnotationLink annotation2 = new PDAnnotationLink();
        PDPageDestination dest2 = new PDPageFitDestination();
        dest2.setPage(oldPage);
        annotation2.setDestination(dest2);

        List<PDAnnotation> annotations = Arrays.asList(annotation, annotation2);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation2), newPage.getAnnotations().get(0));
    }

    @Test
    public void linksGoTo_PageNotRelevant() {

        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(dest);
        annotation.setAction(action);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(0, newPage.getAnnotations().size());
        assertTrue(annotationsLookup.isEmpty());
    }

    @Test
    public void linksGoTo_PageRelevant() {
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(oldPage);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(dest);
        annotation.setAction(action);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation), newPage.getAnnotations().get(0));
    }

    @Test
    public void linksGoTo_OnePageNotRelevantOneRelevant() {
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(dest);
        annotation.setAction(action);

        PDAnnotationLink annotation2 = new PDAnnotationLink();
        PDPageDestination dest2 = new PDPageFitDestination();
        dest2.setPage(oldPage);
        PDActionGoTo action2 = new PDActionGoTo();
        action2.setDestination(dest2);
        annotation2.setAction(action2);

        List<PDAnnotation> annotations = Arrays.asList(annotation, annotation2);
        oldPage.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation2), newPage.getAnnotations().get(0));
    }

    @Test
    public void links_NamedPageNotRelevant() throws IOException {
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDNamedDestination namedDest = new PDNamedDestination(COSName.AESV3);
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        annotation.setDestination(namedDest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = mock(PDDocument.class);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        when(doc.getDocumentCatalog()).thenReturn(catalog);
        when(catalog.findNamedDestinationPage(any(PDNamedDestination.class))).thenReturn(dest);
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(0, newPage.getAnnotations().size());
        assertTrue(annotationsLookup.isEmpty());
    }

    @Test
    public void links_NamedPageRelevant() throws IOException {
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDNamedDestination namedDest = new PDNamedDestination(COSName.AESV3);

        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(oldPage);
        annotation.setDestination(namedDest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        oldPage.setAnnotations(annotations);
        PDDocument doc = mock(PDDocument.class);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        when(doc.getDocumentCatalog()).thenReturn(catalog);
        when(catalog.findNamedDestinationPage(namedDest)).thenReturn(dest);
        doc.addPage(oldPage);
        LookupTable<PDAnnotation> annotationsLookup = new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
        assertEquals(annotationsLookup.lookup(annotation), newPage.getAnnotations().get(0));
    }

    @Test
    public void popupRelevant() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/popup_annotation.pdf")))) {
            PDPage firstOrigin = doc.getPage(0);
            PDPage firstNew = new PDPage();
            lookup.addLookupEntry(firstOrigin, firstNew);
            new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
            List<PDAnnotation> annotations = firstNew.getAnnotations();
            assertFalse(annotations.isEmpty());
            List<PDAnnotationMarkup> parent = annotations.stream().filter(a -> a instanceof PDAnnotationPopup)
                    .map(a -> ((PDAnnotationPopup) a).getParent()).collect(Collectors.toList());
            assertEquals(1, parent.size());
            assertTrue(annotations.contains(parent.get(0)));
        }
    }

    @Test
    public void removePopupIfGarbage() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/popup_annotation.pdf")))) {
            PDPage firstOrigin = doc.getPage(0);
            // let's put some garbage in place of the popup
            firstOrigin.getAnnotations().stream().filter(a -> !(a instanceof PDAnnotationPopup))
                    .forEach(a -> a.getCOSObject().setItem(COSName.POPUP, new PDPage()));
            PDPage firstNew = new PDPage();
            lookup.addLookupEntry(firstOrigin, firstNew);
            new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
            List<PDAnnotation> annotations = firstNew.getAnnotations();
            assertFalse(annotations.isEmpty());

            PDAnnotationMarkup parent = annotations.stream().filter(a -> a instanceof PDAnnotationMarkup)
                    .map(a -> ((PDAnnotationMarkup) a)).findFirst().get();

            assertNull(parent.getPopup());
        }
    }

    @Test
    public void popupRelevantRevertedOrder() throws IOException {
        try (PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/popup_annotation.pdf")))) {
            PDPage firstOrigin = doc.getPage(0);
            List<PDAnnotation> annots = firstOrigin.getAnnotations();
            Collections.reverse(annots);
            firstOrigin.setAnnotations(annots);
            PDPage firstNew = new PDPage();
            lookup.addLookupEntry(firstOrigin, firstNew);
            new AnnotationsDistiller(doc).retainRelevantAnnotations(lookup);
            List<PDAnnotation> annotations = firstNew.getAnnotations();
            assertFalse(annotations.isEmpty());
            List<PDAnnotationMarkup> parent = annotations.stream().filter(a -> a instanceof PDAnnotationPopup)
                    .map(a -> ((PDAnnotationPopup) a).getParent()).collect(Collectors.toList());
            assertEquals(1, parent.size());
            assertTrue(annotations.contains(parent.get(0)));
        }

    }
}
