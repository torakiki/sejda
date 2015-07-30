package org.sejda.impl.sambox.component;

import java.io.IOException;

import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
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
