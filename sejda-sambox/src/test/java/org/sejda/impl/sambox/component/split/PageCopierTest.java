/*
 * Created on 27 giu 2016
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
package org.sejda.impl.sambox.component.split;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.output.ExistingPagesSizePredictor;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.sejda.sambox.pdmodel.graphics.state.PDSoftMask;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDThreadBead;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            new PageCopier(true).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor, times(2)).addPage(captor.capture());
            PDPage copy = captor.getAllValues().get(0);
            PDPage optimizedCopy = captor.getAllValues().get(1);
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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
            assertTrue(copy.getThreadBeads().isEmpty());
        }
    }

    @Test
    public void removesParentAndPopup() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/popup_annotation.pdf")))) {
            PDPage page = document.getPage(0);
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
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
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            new PageCopier(false).addCopyTo(page, predictor);
            ArgumentCaptor<PDPage> captor = ArgumentCaptor.forClass(PDPage.class);
            verify(predictor).addPage(captor.capture());
            PDPage copy = captor.getValue();
            assertNull(copy.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSArray.class)
                    .getObject(0, COSStream.class).getItem(COSName.ANNOTS));
        }
    }

    @Test
    // form resources have the same Font, ExtGState and XObject dictionaries of the pages
    public void optimizedCopyKeepsSourceFormSharingNameDictionariesWithPages() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_name_dics_page_and_form.pdf")));
                ExistingPagesSizePredictor predictor = ExistingPagesSizePredictor.instance()) {
            COSStream form = document.getPage(0).getResources().getCOSObject()
                    .getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("Fm"), COSStream.class);
            COSBase formResources = form.getItem(COSName.RESOURCES);
            new PageCopier(true).addCopyTo(document.getPage(0), predictor);
            assertSame(formResources, form.getItem(COSName.RESOURCES));
            PDResources page1Res = document.getPage(0).getResources();
            assertNotNull(page1Res.getFont(COSName.getPDFName("F1")));
            assertNotNull(page1Res.getExtGState(COSName.getPDFName("GS1")));
            assertTrue(page1Res.isImageXObject(COSName.getPDFName("Im1")));
            assertTrue(page1Res.isFormXObject(COSName.getPDFName("Fm")));
            PDResources formRes = ((PDFormXObject) page1Res.getXObject(COSName.getPDFName("Fm"))).getResources();
            assertNotNull(formRes.getFont(COSName.getPDFName("FN")));
            assertNotNull(formRes.getExtGState(COSName.getPDFName("GSN")));
            assertTrue(formRes.isImageXObject(COSName.getPDFName("ImN")));
            PDResources page2Res = document.getPage(1).getResources();
            assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
            assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
            assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
        }
    }

    @Test
    // form resources are the same resource dictionary of the pages
    public void optimizedCopyKeepsSourceFormSharingPageResources() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_res_dic_page_and_form.pdf")));
                ExistingPagesSizePredictor predictor = ExistingPagesSizePredictor.instance()) {
            new PageCopier(true).addCopyTo(document.getPage(0), predictor);
            PDResources page1Res = document.getPage(0).getResources();
            assertNotNull(page1Res.getFont(COSName.getPDFName("F1")));
            assertNotNull(page1Res.getExtGState(COSName.getPDFName("GS1")));
            assertTrue(page1Res.isImageXObject(COSName.getPDFName("Im1")));
            assertTrue(page1Res.isFormXObject(COSName.getPDFName("Fm")));
            PDResources formRes = ((PDFormXObject) page1Res.getXObject(COSName.getPDFName("Fm"))).getResources();
            assertNotNull(formRes.getFont(COSName.getPDFName("FN")));
            assertNotNull(formRes.getExtGState(COSName.getPDFName("GSN")));
            assertTrue(formRes.isImageXObject(COSName.getPDFName("ImN")));
            PDResources page2Res = document.getPage(1).getResources();
            assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
            assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
            assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
        }
    }

    @Test
    // soft mask group resources have the same Font, ExtGState and XObject dictionaries of the pages
    public void optimizedCopyKeepsSourceSoftMaskSharingNameDictionariesWithPages() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_name_dics_page_and_softmask.pdf")));
                ExistingPagesSizePredictor predictor = ExistingPagesSizePredictor.instance()) {
            new PageCopier(true).addCopyTo(document.getPage(0), predictor);
            PDResources page1Res = document.getPage(0).getResources();
            assertNotNull(page1Res.getFont(COSName.getPDFName("F1")));
            assertNotNull(page1Res.getExtGState(COSName.getPDFName("GS1")));
            assertTrue(page1Res.isImageXObject(COSName.getPDFName("Im1")));
            PDExtendedGraphicsState masked = page1Res.getExtGState(COSName.getPDFName("GSM"));
            assertNotNull(masked);
            PDSoftMask softMask = masked.getSoftMask();
            assertNotNull(softMask);
            PDResources maskRes = softMask.getGroup().getResources();
            assertNotNull(maskRes.getFont(COSName.getPDFName("FN")));
            assertNotNull(maskRes.getExtGState(COSName.getPDFName("GSN")));
            assertTrue(maskRes.isImageXObject(COSName.getPDFName("ImN")));
            PDResources page2Res = document.getPage(1).getResources();
            assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
            assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
            assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
        }
    }

    @Test
    // form FmA on page 1 and form FmB on page 2 have the same resource dictionary
    public void optimizedCopyKeepsSourceFormsSharingResources() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_res_dic_between_forms.pdf")));
                ExistingPagesSizePredictor predictor = ExistingPagesSizePredictor.instance()) {
            new PageCopier(true).addCopyTo(document.getPage(0), predictor);
            PDResources page1Res = document.getPage(0).getResources();
            assertTrue(page1Res.isFormXObject(COSName.getPDFName("FmA")));
            PDResources formARes = ((PDFormXObject) page1Res.getXObject(COSName.getPDFName("FmA"))).getResources();
            assertNotNull(formARes.getFont(COSName.getPDFName("F1")));
            assertNotNull(formARes.getExtGState(COSName.getPDFName("GS1")));
            assertTrue(formARes.isImageXObject(COSName.getPDFName("Im1")));
            PDResources page2Res = document.getPage(1).getResources();
            assertTrue(page2Res.isFormXObject(COSName.getPDFName("FmB")));
            PDResources formBRes = ((PDFormXObject) page2Res.getXObject(COSName.getPDFName("FmB"))).getResources();
            assertNotNull(formBRes.getFont(COSName.getPDFName("F2")));
            assertNotNull(formBRes.getExtGState(COSName.getPDFName("GS2")));
            assertTrue(formBRes.isImageXObject(COSName.getPDFName("Im2")));
        }
    }

    @Test
    public void optimizedCopyRestoresSourceWhenPredictionFails() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_res_dic_page_and_form.pdf")))) {
            COSStream form = document.getPage(0).getResources().getCOSObject()
                    .getDictionaryObject(COSName.XOBJECT, COSDictionary.class)
                    .getDictionaryObject(COSName.getPDFName("Fm"), COSStream.class);
            COSBase formResources = form.getItem(COSName.RESOURCES);
            ExistingPagesSizePredictor predictor = mock(ExistingPagesSizePredictor.class);
            doThrow(new IOException()).when(predictor).addPage(any());
            assertThrows(IOException.class, () -> new PageCopier(true).addCopyTo(document.getPage(0), predictor));
            assertSame(formResources, form.getItem(COSName.RESOURCES));
            PDResources page2Res = document.getPage(1).getResources();
            assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
            assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
            assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
        }
    }

}
