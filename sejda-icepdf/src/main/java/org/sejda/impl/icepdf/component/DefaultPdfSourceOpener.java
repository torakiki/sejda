/*
 * Created on 16/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.icepdf.component;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.icepdf.core.SecurityCallback;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskWrongPasswordException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;

/**
 * ICEpdf component able to open a PdfSource and return the corresponding {@link Document}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfSourceOpener implements PdfSourceOpener<Document> {

    private static final String AN_ERROR_OCCURRED_OPENING_SOURCE = "An error occurred opening the source: %s.";

    public Document open(PdfURLSource source) throws TaskIOException {
        Document document = newDocument(source);
        try {
            document.setUrl(source.getSource());
        } catch (PDFException e) {
            throw new TaskIOException(String.format(AN_ERROR_OCCURRED_OPENING_SOURCE, source), e);
        } catch (PDFSecurityException e) {
            throw new TaskWrongPasswordException(String.format("An error occurred decrypting the source: %s.", source),
                    e);
        } catch (IOException e) {
            throw new TaskIOException(String.format(AN_ERROR_OCCURRED_OPENING_SOURCE, source), e);
        }
        return document;
    }

    public Document open(PdfFileSource source) throws TaskIOException {
        Document document = newDocument(source);
        try {
            document.setFile(source.getSource().getAbsolutePath());
        } catch (PDFException e) {
            throw new TaskIOException(String.format(AN_ERROR_OCCURRED_OPENING_SOURCE, source), e);
        } catch (PDFSecurityException e) {
            throw new TaskWrongPasswordException(String.format("An error occurred decrypting the source: %s.", source),
                    e);
        } catch (IOException e) {
            throw new TaskIOException(String.format(AN_ERROR_OCCURRED_OPENING_SOURCE, source), e);
        }
        return document;
    }

    public Document open(PdfStreamSource source) throws TaskIOException {
        Document document = newDocument(source);
        try {
            document.setInputStream(source.getSource(), source.getName());
        } catch (PDFException e) {
            throw new TaskIOException(String.format(AN_ERROR_OCCURRED_OPENING_SOURCE, source), e);
        } catch (PDFSecurityException e) {
            throw new TaskWrongPasswordException(String.format("An error occurred decrypting the source: %s.", source),
                    e);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An I/O error occurred opening the source: %s.", source), e);
        }
        return document;
    }

    private Document newDocument(final PdfSource<?> source) {
        Document document = new Document();
        if (StringUtils.isNotBlank(source.getPassword())) {
            document.setSecurityCallback(new PdfSourceSecurityCallback(source));
        }
        return document;
    }

    /**
     * ICEpdf security callback binded to a {@link PdfSource}.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class PdfSourceSecurityCallback implements SecurityCallback {
        private final PdfSource<?> source;

        private PdfSourceSecurityCallback(PdfSource<?> source) {
            this.source = source;
        }

        public String requestPassword(Document document) {
            return source.getPassword();
        }
    }
}
