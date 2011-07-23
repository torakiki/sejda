package org.sejda.core.manipulation.model.task.itext.component.input;

import java.io.IOException;

import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * iText component able to open a PdfSource and return the corresponding {@link PdfReader} opened in "partial" mode.
 * 
 * @author Andrea Vacondio
 * 
 */
class PartialReadPdfSourceOpener extends AbstractPdfSourceOpener {

    @Override
    PdfReader openSource(PdfURLSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getUrl()), source.getPasswordBytes());
    }

    @Override
    PdfReader openSource(PdfFileSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getFile().getAbsolutePath()), source.getPasswordBytes());
    }

    @Override
    PdfReader openSource(PdfStreamSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getStream()), source.getPasswordBytes());
    }

}
