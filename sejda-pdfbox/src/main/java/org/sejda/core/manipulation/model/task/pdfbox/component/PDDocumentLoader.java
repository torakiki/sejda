package org.sejda.core.manipulation.model.task.pdfbox.component;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;
import org.sejda.core.manipulation.model.task.pdfbox.util.PDDocumentUtil;

/**
 * PDFBox component able to open a PdfSource and return the corresponding {@link PDDocument}
 * 
 * @author Andrea Vacondio
 * 
 */
public class PDDocumentLoader implements PdfSourceOpener<PDDocument> {

    public PDDocument open(PdfURLSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getUrl());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        PDDocumentUtil.decryptPDDocumentIfNeeded(document, source.getPassword());
        return document;
    }

    public PDDocument open(PdfFileSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getFile());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        PDDocumentUtil.decryptPDDocumentIfNeeded(document, source.getPassword());
        return document;
    }

    public PDDocument open(PdfStreamSource source) throws TaskIOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(source.getStream());
        } catch (IOException e) {
            throw new TaskIOException(String.format("An error occurred opening the source: %s.", source), e);
        }
        PDDocumentUtil.decryptPDDocumentIfNeeded(document, source.getPassword());
        return document;
    }
}
