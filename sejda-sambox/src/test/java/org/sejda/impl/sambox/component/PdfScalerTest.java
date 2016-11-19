/*
 * Created on 18 nov 2016
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

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.scale.ScaleType;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfScalerTest {

    @Test
    public void shrinkContentNoBoxes() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PdfScaler scaler = new PdfScaler(ScaleType.CONTENT);
            scaler.scale(document, 0.5);
            PDPage page = document.getPage(0);
            assertEquals(new PDRectangle(0, 0, 595, 842), page.getMediaBox());
            assertEquals(new PDRectangle(0, 0, 595, 842), page.getCropBox());
            assertEquals(new PDRectangle(148.75f, 210.5f, 297.5f, 421f), page.getArtBox());
            assertEquals(new PDRectangle(148.75f, 210.5f, 297.5f, 421f), page.getBleedBox());
            assertEquals(new PDRectangle(148.75f, 210.5f, 297.5f, 421f), page.getTrimBox());
        }
    }

    @Test
    public void shrinkContentYesBoxes() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PDPage page = document.getPage(0);
            PDRectangle yesBox = new PDRectangle(0, 0, 200, 400);
            PDRectangle cropBox = new PDRectangle(0, 0, 400, 800);
            page.setCropBox(cropBox);
            page.setArtBox(yesBox);
            page.setTrimBox(yesBox);
            page.setBleedBox(yesBox);
            PdfScaler scaler = new PdfScaler(ScaleType.CONTENT);
            scaler.scale(document, 0.5);

            assertEquals(new PDRectangle(0, 0, 595, 842), page.getMediaBox());
            assertEquals(cropBox, page.getCropBox());
            assertEquals(new PDRectangle(150, 300, 100, 200), page.getArtBox());
            assertEquals(new PDRectangle(150, 300, 100, 200), page.getBleedBox());
            assertEquals(new PDRectangle(150, 300, 100, 200), page.getTrimBox());
        }
    }

    @Test
    public void expandContentNoBoxes() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PdfScaler scaler = new PdfScaler(ScaleType.CONTENT);
            scaler.scale(document, 1.5);
            PDPage page = document.getPage(0);
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getMediaBox());
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getCropBox());
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getArtBox());
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getBleedBox());
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getTrimBox());
        }
    }

    @Test
    public void expandContentYesBoxesOverflow() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PDPage page = document.getPage(0);
            PDRectangle yesBox = new PDRectangle(0, 0, 200, 400);
            PDRectangle cropBox = new PDRectangle(0, 0, 400, 800);
            page.setCropBox(cropBox);
            page.setArtBox(yesBox);
            page.setTrimBox(yesBox);
            page.setBleedBox(yesBox);
            PdfScaler scaler = new PdfScaler(ScaleType.CONTENT);
            scaler.scale(document, 2.5);
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getMediaBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getCropBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getArtBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getBleedBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getTrimBox());
        }

    }

    @Test
    public void expandContentYesBoxesNoOverflow() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PDPage page = document.getPage(0);
            PDRectangle yesBox = new PDRectangle(0, 0, 200, 400);
            PDRectangle cropBox = new PDRectangle(0, 0, 400, 800);
            page.setCropBox(cropBox);
            page.setArtBox(yesBox);
            page.setTrimBox(yesBox);
            page.setBleedBox(yesBox);
            PdfScaler scaler = new PdfScaler(ScaleType.CONTENT);
            scaler.scale(document, 1.5);
            assertEquals(new PDRectangle(0, 0, 595f, 842), page.getMediaBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getCropBox());
            assertEquals(new PDRectangle(50, 100, 300, 600), page.getArtBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getBleedBox());
            assertEquals(new PDRectangle(0, 0, 400, 800), page.getTrimBox());
        }
    }

    @Test
    public void shrinkPageNoBoxes() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PdfScaler scaler = new PdfScaler(ScaleType.PAGE);
            scaler.scale(document, 0.5);
            PDPage page = document.getPage(0);
            PDRectangle expected = new PDRectangle(0f, 0f, 297.5f, 421f);
            assertEquals(expected, page.getMediaBox());
            assertEquals(expected, page.getCropBox());
            assertEquals(expected, page.getArtBox());
            assertEquals(expected, page.getBleedBox());
            assertEquals(expected, page.getTrimBox());
        }
    }

    @Test
    public void expandPageNoBoxes() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PdfScaler scaler = new PdfScaler(ScaleType.PAGE);
            scaler.scale(document, 1.5);
            PDPage page = document.getPage(0);
            PDRectangle expected = new PDRectangle(0, 0, 892.5f, 1263f);
            assertEquals(expected, page.getMediaBox());
            assertEquals(expected, page.getCropBox());
            assertEquals(expected, page.getTrimBox());
            assertEquals(expected, page.getArtBox());
            assertEquals(expected, page.getBleedBox());
        }
    }

    @Test
    public void expandPagesYesBoxes() throws IOException, TaskIOException {
        try (PDDocument document = PDFParser.parse(SeekableSources
                .inMemorySeekableSourceFrom(getClass().getClassLoader().getResourceAsStream("pdf/one_page.pdf")))) {
            PDPage page = document.getPage(0);
            PDRectangle yesBox = new PDRectangle(0, 0, 200, 400);
            PDRectangle cropBox = new PDRectangle(0, 0, 400, 800);
            page.setCropBox(cropBox);
            page.setArtBox(yesBox);
            page.setTrimBox(yesBox);
            page.setBleedBox(yesBox);
            PdfScaler scaler = new PdfScaler(ScaleType.PAGE);
            scaler.scale(document, 1.5);

            assertEquals(new PDRectangle(0, 0, 892.5f, 1263f), page.getMediaBox());
            assertEquals(new PDRectangle(0, 0, 600, 1200), page.getCropBox());
            assertEquals(new PDRectangle(0, 0, 300, 600), page.getArtBox());
            assertEquals(new PDRectangle(0, 0, 300, 600), page.getBleedBox());
            assertEquals(new PDRectangle(0, 0, 300, 600), page.getTrimBox());
        }
    }
}
