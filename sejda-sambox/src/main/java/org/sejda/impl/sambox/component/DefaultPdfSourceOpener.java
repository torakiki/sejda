/* 
 * This file is part of the Sejda source code
 * Created on 31/Jul/2015
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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

import java.io.IOException;

import org.sejda.io.SeekableSources;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * PDFBox component able to open a PdfSource and return the corresponding {@link PDDocumentHandler}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfSourceOpener implements PdfSourceOpener<PDDocumentHandler> {

    public PDDocumentHandler open(PdfURLSource source) throws TaskIOException {
        try {
            PDDocument document = PDFParser.parse(SeekableSources.onTempFileSeekableSourceFrom(source.getSource().openStream()), source.getPassword());
            return new PDDocumentHandler(document);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
    }

    public PDDocumentHandler open(PdfFileSource source) throws TaskIOException {
        try {
            PDDocument document = PDFParser.parse(SeekableSources.seekableSourceFrom(source.getSource()), source.getPassword());
            return new PDDocumentHandler(document);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
    }

    public PDDocumentHandler open(PdfStreamSource source) throws TaskIOException {
        try {
            PDDocument document = PDFParser.parse(SeekableSources.onTempFileSeekableSourceFrom(source.getSource()), source.getPassword());
            return new PDDocumentHandler(document);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }

    }
}
