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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;

/**
 * Component responsible for handling operations related to a {@link PdfCopy} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfCopyHandler {

    private PdfCopy pdfCopy = null;
    private FileOutputStream ouputStream = null;
    private Document pdfDocument = null;

    /**
     * Creates a new instance initializing the inner {@link PdfCopy} instance.
     * 
     * @param reader
     *            input reader
     * @param ouputFile
     *            {@link File} to copy on
     * @param version
     *            version for the created pod copy, if null the version number is taken from the input {@link PdfReader}
     * @throws TaskException
     *             in case of error
     */
    public PdfCopyHandler(PdfReader reader, File ouputFile, PdfVersion version) throws TaskException {
        try {
            ouputStream = new FileOutputStream(ouputFile);
            pdfDocument = new Document(reader.getPageSizeWithRotation(1));
            if (version == null) {
                pdfCopy = new PdfCopy(pdfDocument, ouputStream);
                pdfCopy.setPdfVersion(reader.getPdfVersion());
            } else {
                pdfCopy = new PdfCopy(pdfDocument, ouputStream);
                pdfCopy.setPdfVersion(version.getVersionAsCharacter());
            }
            pdfDocument.addCreator(Sejda.CREATOR);
            pdfDocument.open();
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfCopy.", e);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred opening the PdfCopy.", e);
        }
    }

    /**
     * Adds to the {@link PdfCopy} the given page extracted from the input reader
     * 
     * @param reader
     * @param pageNumber
     * @throws TaskException
     */
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
     * Enables compression if compress is true
     * 
     * @param compress
     */
    public void setCompressionOnCopier(boolean compress) {
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

    /**
     * Closes the copier suppressing the exception.
     * 
     */
    public void closePdfCopier() {
        pdfDocument.close();
        pdfCopy.close();
        IOUtils.closeQuietly(ouputStream);
    }
}
