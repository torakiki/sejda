/*
 * Created on 25/dic/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.task.itext.component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.sejda.core.Sejda;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

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
            if (version == null) {
                pdfCopy = new PdfSmartCopy(pdfDocument, outputStream);
                pdfCopy.setPdfVersion(reader.getPdfVersion());
            } else {
                pdfCopy = new PdfSmartCopy(pdfDocument, outputStream);
                pdfCopy.setPdfVersion(version.getVersionAsCharacter());
            }
            pdfDocument.addCreator(Sejda.CREATOR);
            pdfDocument.open();
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfCopy.", e);
        }
    }

    public void addPage(PdfReader reader, int pageNumber) throws TaskException {
        try {
            pdfCopy.addPage(pdfCopy.getImportedPage(reader, pageNumber));
            pdfDocument.open();
        } catch (BadPdfFormatException e) {
            throw new TaskException(String.format("An error occurred adding page %d to the PdfCopy.", pageNumber), e);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An IO error occurred adding page %d to the PdfCopy.", pageNumber),
                    e);
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
            throw new TaskIOException("An IO error occurred adding page %d to the PdfCopy.", e);
        }
    }

    public void setPageLabels(PdfPageLabels labels) {
        pdfCopy.setPageLabels(labels);
    }

    public void close() {
        pdfDocument.close();
        pdfCopy.close();
    }

    public void setBookmarks(List<?> bookmarks) {
        if (bookmarks != null) {
            pdfCopy.setOutlines(bookmarks);
        }
    }
}
