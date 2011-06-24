/*
 * Created on 13/nov/2010
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
package org.sejda.core.manipulation.model.task.pdfbox.util;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.BadSecurityHandlerException;
import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Vacondio
 * 
 */
public final class PDDocumentIOUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PDDocumentIOUtil.class);

    private PDDocumentIOUtil() {
        // utility class
    }

    /**
     * Closes the input {@link PDDocument}
     * 
     * @param document
     */
    public static void closePDDocumentQuitely(PDDocument document) {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                LOG.warn("Unable to close the input document", e);
            }
        }
    }

    /**
     * Saves the input document removing security from it.
     * 
     * @param document
     * @param file
     * @throws TaskException
     */
    public static void saveDecryptedPDDocument(PDDocument document, File file) throws TaskException {
        savePDDocument(document, file, true);
    }

    /**
     * Saves the input document to the given file
     * 
     * @param document
     * @param file
     * @throws TaskException
     */
    public static void savePDDocument(PDDocument document, File file) throws TaskException {
        savePDDocument(document, file, false);
    }

    private static void savePDDocument(PDDocument document, File file, boolean decrypted) throws TaskException {
        if (document != null) {
            try {
                if (decrypted) {
                    document.setAllSecurityToBeRemoved(decrypted);
                }
                document.save(file.getAbsolutePath());
            } catch (COSVisitorException e) {
                throw new TaskException("An error occured saving to temporary file.", e);
            } catch (IOException e) {
                throw new TaskIOException("Unable to save to temporary file.", e);
            }
        }
    }

    /**
     * Loads a {@link PDDocument} from the input {@link PdfSource}
     * 
     * @param source
     *            from where the {@link PDDocument} will be loaded.
     * @return the loaded {@link PDDocument}
     * @throws TaskIOException
     *             if an error occur during document load.
     */
    public static PDDocument loadPDDocument(PdfSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            switch (source.getSourceType()) {
            case FILE_SOURCE:
                document = PDDocument.load(((PdfFileSource) source).getFile());
                break;
            case STREAM_SOURCE:
                document = PDDocument.load(((PdfStreamSource) source).getStream());
                break;
            case URL_SOURCE:
                document = PDDocument.load(((PdfURLSource) source).getUrl());
                break;
            default:
                throw new TaskIOException("Unable to identify the input pdf source.");
            }
            PDDocumentUtil.decryptPDDocumentIfNeeded(document, source.getPassword());
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        } catch (BadSecurityHandlerException e) {
            throw new TaskIOException("Unable to open the document.", e);
        } catch (CryptographyException e) {
            throw new TaskIOException("Unable to open the document.", e);
        }
        return document;
    }
}
