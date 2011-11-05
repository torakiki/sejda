package org.sejda.impl.pdfbox.component;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;

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
            document = PDDocument.load(source.getSource());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        return new PDDocumentHandler(document, source.getPassword());
    }

    public PDDocumentHandler open(PdfFileSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getSource());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        return new PDDocumentHandler(document, source.getPassword());
    }

    public PDDocumentHandler open(PdfStreamSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getSource());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        return new PDDocumentHandler(document, source.getPassword());
    }
}
