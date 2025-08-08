/*
 * Copyright 2024 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.sambox.component;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;

import java.io.IOException;

public class ReloadablePDDocumentHandler {
    
    private final PdfSource<?> source;
    private final PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler documentHandler;

    public ReloadablePDDocumentHandler(PdfSource<?> source, PdfSourceOpener<PDDocumentHandler> documentLoader) throws TaskIOException {
        this.source = source;
        this.documentLoader = documentLoader;
        this.documentHandler = this.source.open(documentLoader);
    }
    
    public PDDocumentHandler reload() throws TaskIOException {
        try {
            documentHandler.close();
        } catch (IOException e) {
            // noop
        }

        this.documentHandler = this.source.open(documentLoader);
        return this.documentHandler;
    }

    public PDDocumentHandler getDocumentHandler() {
        return documentHandler;
    }
}
