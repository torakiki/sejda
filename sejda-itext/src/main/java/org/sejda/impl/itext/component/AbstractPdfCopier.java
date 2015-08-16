/*
 * Created on 25/dic/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfVersion;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSmartCopy;
import com.lowagie.text.pdf.PdfStream;

/**
 * Abstract implementation using an underlying {@link PdfSmartCopy} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class AbstractPdfCopier implements PdfCopier {

    private PdfSmartCopy pdfCopy = null;
    private Document pdfDocument = null;
    private boolean closed = false;
    private int numberOfCopiedPages = 0;

    /**
     * Opens the copier using the given reader and the given output version.
     * 
     * @param reader
     * @param outputStream
     *            the output stream to write to.
     * @param version
     *            version for the created pdf copy, if null the version number is taken from the input {@link PdfReader}
     */
    void open(PdfReader reader, OutputStream outputStream, PdfVersion version) throws TaskException {
        try {
            pdfDocument = new Document(reader.getPageSizeWithRotation(1));
            pdfCopy = new PdfSmartCopy(pdfDocument, outputStream);
            if (version == null) {
                pdfCopy.setPdfVersion(reader.getPdfVersion());
            } else {
                pdfCopy.setPdfVersion(version.getVersionAsCharacter());
            }
            pdfDocument.addCreator(Sejda.CREATOR);
            pdfDocument.open();
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfSmartCopy.", e);
        }
    }

    public void addPage(PdfReader reader, int pageNumber) throws TaskException {
        try {
            pdfCopy.addPage(pdfCopy.getImportedPage(reader, pageNumber));
            numberOfCopiedPages++;
        } catch (BadPdfFormatException e) {
            throw new TaskException(String.format("An error occurred adding page %d to the PdfSmartCopy.", pageNumber),
                    e);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An IO error occurred adding page %d to the PdfSmartCopy.",
                    pageNumber), e);
        }
    }

    public void addBlankPage(PdfReader reader) {
        pdfCopy.addPage(reader.getPageSize(1), reader.getPageRotation(1));
        numberOfCopiedPages++;
    }

    public void addBlankPageIfOdd(PdfReader reader) {
        if (reader.getNumberOfPages() % 2 != 0) {
            addBlankPage(reader);
        }
    }

    /**
     * Adds to the {@link PdfSmartCopy} all the pages from the input reader
     * 
     * @param reader
     * @throws TaskException
     */
    public void addAllPages(PdfReader reader) throws TaskException {
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            addPage(reader, i);
        }
    }

    public void setCompression(boolean compress) {
        if (compress) {
            pdfCopy.setFullCompression();
            pdfCopy.setCompressionLevel(PdfStream.BEST_COMPRESSION);
        }
    }

    public void freeReader(PdfReader reader) throws TaskIOException {
        try {
            pdfCopy.freeReader(reader);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred freeing the pdf reader.", e);
        }
    }

    public void setPageLabels(PdfPageLabels labels) {
        pdfCopy.setPageLabels(labels);
    }

    public void close() {
        if (pdfDocument != null) {
            pdfDocument.close();
        }
        if (pdfCopy != null) {
            pdfCopy.close();
        }
        closed = true;
    }

    public void setOutline(List<Map<String, Object>> outline) {
        if (outline != null && !outline.isEmpty()) {
            pdfCopy.setOutlines(outline);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public int getNumberOfCopiedPages() {
        return numberOfCopiedPages;
    }
}
