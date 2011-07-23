package org.sejda.core.manipulation.model.task.itext.component.input;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;

import com.lowagie.text.pdf.PdfReader;

/**
 * iText component able to open a PdfSource and return the corresponding {@link PdfReader}. The opened input source is fully loaded into memory.
 * 
 * @author Andrea Vacondio
 * 
 */
// AV: this loader is used in the RotateTask because when using the partial read loader the rotation is not applied (I don't know why). I tried to dig into the iText code but it
// looks like a treasure map to me, I can't follow it and I'm not able to find why rotation is not working. As a workaround we use this loader in the RotateTask that seems
// working.
class FullReadPdfSourceOpener extends AbstractPdfSourceOpener {

    @Override
    PdfReader openSource(PdfURLSource source) throws IOException {
        return new PdfReader(new BufferedInputStream(source.getUrl().openStream()), source.getPasswordBytes());

    }

    @Override
    PdfReader openSource(PdfFileSource source) throws IOException {
        return new PdfReader(new BufferedInputStream(new FileInputStream(source.getFile())), source.getPasswordBytes());

    }

    @Override
    PdfReader openSource(PdfStreamSource source) throws IOException {
        return new PdfReader(new BufferedInputStream(source.getStream()), source.getPasswordBytes());
    }

}
