package org.sejda.impl.pdfbox.component;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;

/**
 * PDFBox component able to open a PdfSource and return the corresponding {@link PDDocumentHandler}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfSourceOpener implements PdfSourceOpener<PDDocumentHandler> {

    public PDDocumentHandler open(PdfURLSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getUrl());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        return newHandlerInstance(source, document);
    }

    public PDDocumentHandler open(PdfFileSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getFile());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        return newHandlerInstance(source, document);
    }

    public PDDocumentHandler open(PdfStreamSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getStream());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        return newHandlerInstance(source, document);
    }

    private PDDocumentHandler newHandlerInstance(PdfSource source, PDDocument document) throws TaskIOException {
        PDDocumentHandler handler = new PDDocumentHandler(document);
        handler.decryptPDDocumentIfNeeded(source.getPassword());
        handler.setCreatorOnPDDocument();
        return handler;
    }
}
