/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.pdfbox.component;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

import java.io.Closeable;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
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

    public void addPage(PDDocument original, int pageNumber) throws TaskIOException {
        PDPage page = (PDPage) original.getDocumentCatalog().getAllPages().get(pageNumber - 1);
        document.importPage(page);
    }

    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    public void saveToFile() throws TaskException {
        document.saveDecryptedPDDocument(outputFile);
    }

    @Override
    public void close() {
        nullSafeCloseQuietly(document);
    }
}
