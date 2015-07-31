/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.pdfbox.component;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

import java.io.Closeable;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;

public class PdfCopier implements Closeable {
    PDDocumentHandler document;
    File outputFile;
    PdfVersion pdfVersion;

    boolean compression;

    public PdfCopier(PDDocument original, File outputFile, PdfVersion pdfVersion) {
        this.document = new PDDocumentHandler();
        this.outputFile = outputFile;
        this.pdfVersion = pdfVersion;

        document.setDocumentInformation(original.getDocumentInformation());
        document.setViewerPreferences(original.getDocumentCatalog().getViewerPreferences());
    }

    void addPage(PDDocument original, int pageNumber) throws TaskException {
        PDPage page = (PDPage) original.getDocumentCatalog().getAllPages().get(pageNumber - 1);
        document.importPage(page);
    }

    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    public void saveToFile() throws TaskException {
        document.saveDecryptedPDDocument(outputFile);
    }

    public void close() {
        nullSafeCloseQuietly(document);
    }
}
