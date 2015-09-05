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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.action.PDActionJavaScript;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationText;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;

/**
 * @author Andrea Vacondio
 *
 */
public class AnnotationsDistillerTest {

    @Test(expected = IllegalArgumentException.class)
    public void fiterNullDocument() {
        AnnotationsDistiller.filterAnnotations(Collections.emptySet(), null);
    }

    @Test
    public void noLinks() throws IOException {
        PDPage page = new PDPage();
        List<PDAnnotation> annotations = Arrays.asList(new PDAnnotationText());
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(annotations.get(0).getCOSObject(), page.getAnnotations().get(0).getCOSObject());
    }

    public void noLinks_PageRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationText annotation = new PDAnnotationText();
        annotation.setPage(destPage);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page, destPage)), doc);
        assertEquals(annotations.get(0).getCOSObject(), page.getAnnotations().get(0).getCOSObject());
    }

    @Test
    public void noLinks_PageNotRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationText annotation = new PDAnnotationText();
        annotation.setPage(destPage);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(0, page.getAnnotations().size());
    }

    @Test
    public void noLinks_OnePageNotRelevantOneRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationText annotation = new PDAnnotationText();
        annotation.setPage(destPage);
        PDAnnotationText annotation2 = new PDAnnotationText();
        annotation2.setPage(page);
        List<PDAnnotation> annotations = Arrays.asList(annotation, annotation2);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(1, page.getAnnotations().size());
    }

    @Test
    public void linksNoGoTo() throws IOException {
        PDPage page = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        annotation.setAction(new PDActionJavaScript());
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(1, page.getAnnotations().size());
    }

    @Test
    public void links_PageNotRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        annotation.setDestination(dest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(0, page.getAnnotations().size());
    }

    @Test
    public void links_PageRelevant() throws IOException {
        PDPage page = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(page);
        annotation.setDestination(dest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(annotations.get(0).getCOSObject(), page.getAnnotations().get(0).getCOSObject());
    }

    @Test
    public void links_OnePageNotRelevantOneRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        annotation.setDestination(dest);

        PDAnnotationLink annotation2 = new PDAnnotationLink();
        PDPageDestination dest2 = new PDPageFitDestination();
        dest2.setPage(page);
        annotation2.setDestination(dest2);

        List<PDAnnotation> annotations = Arrays.asList(annotation, annotation2);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(1, page.getAnnotations().size());
    }

    @Test
    public void linksGoTo_PageNotRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(dest);
        annotation.setAction(action);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(0, page.getAnnotations().size());
    }

    @Test
    public void linksGoTo_PageRelevant() throws IOException {
        PDPage page = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(page);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(dest);
        annotation.setAction(action);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(annotations.get(0).getCOSObject(), page.getAnnotations().get(0).getCOSObject());
    }

    @Test
    public void linksGoTo_OnePageNotRelevantOneRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        PDActionGoTo action = new PDActionGoTo();
        action.setDestination(dest);
        annotation.setAction(action);

        PDAnnotationLink annotation2 = new PDAnnotationLink();
        PDPageDestination dest2 = new PDPageFitDestination();
        dest2.setPage(page);
        PDActionGoTo action2 = new PDActionGoTo();
        action2.setDestination(dest2);
        annotation2.setAction(action2);

        List<PDAnnotation> annotations = Arrays.asList(annotation, annotation2);
        page.setAnnotations(annotations);
        PDDocument doc = new PDDocument();
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(1, page.getAnnotations().size());
    }

    @Test
    public void links_NamedPageNotRelevant() throws IOException {
        PDPage page = new PDPage();
        PDPage destPage = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDNamedDestination namedDest = new PDNamedDestination(COSName.AESV3);
        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(destPage);
        annotation.setDestination(namedDest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = mock(PDDocument.class);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        when(doc.getDocumentCatalog()).thenReturn(catalog);
        when(catalog.findNamedDestinationPage(any(PDNamedDestination.class))).thenReturn(dest);
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(0, page.getAnnotations().size());
    }

    @Test
    public void links_NamedPageRelevant() throws IOException {
        PDPage page = new PDPage();
        PDAnnotationLink annotation = new PDAnnotationLink();
        PDNamedDestination namedDest = new PDNamedDestination(COSName.AESV3);

        PDPageDestination dest = new PDPageFitDestination();
        dest.setPage(page);
        annotation.setDestination(namedDest);
        List<PDAnnotation> annotations = Arrays.asList(annotation);
        page.setAnnotations(annotations);
        PDDocument doc = mock(PDDocument.class);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        when(doc.getDocumentCatalog()).thenReturn(catalog);
        when(catalog.findNamedDestinationPage(namedDest)).thenReturn(dest);
        doc.addPage(page);
        AnnotationsDistiller.filterAnnotations(new HashSet<>(Arrays.asList(page)), doc);
        assertEquals(annotations.get(0).getCOSObject(), page.getAnnotations().get(0).getCOSObject());
    }
}
