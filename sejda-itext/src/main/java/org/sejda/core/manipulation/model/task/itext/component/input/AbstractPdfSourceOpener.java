package org.sejda.core.manipulation.model.task.itext.component.input;

import java.io.IOException;

import org.sejda.core.exception.TaskIOException;
import org.sejda.core.exception.TaskWrongPasswordException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;

import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract implementation for a PdfSourceOpener returning a {@link PdfReader}. Subclasses have to provide implementation of the actual source open creating a {@link PdfReader}.
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class AbstractPdfSourceOpener implements PdfSourceOpener<PdfReader> {

    public PdfReader open(PdfURLSource source) throws TaskIOException {
        PdfReader reader;
        try {
            reader = openSource(source);
        } catch (BadPasswordException bpe) {
            throw new TaskWrongPasswordException("Unable to open the document due to a wrong password.", bpe);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    public PdfReader open(PdfFileSource source) throws TaskIOException {
        PdfReader reader;
        try {
            reader = openSource(source);
        } catch (BadPasswordException bpe) {
            throw new TaskWrongPasswordException("Unable to open the document due to a wrong password.", bpe);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    public PdfReader open(PdfStreamSource source) throws TaskIOException {
        PdfReader reader;
        try {
            reader = openSource(source);
        } catch (BadPasswordException bpe) {
            throw new TaskWrongPasswordException("Unable to open the document due to a wrong password.", bpe);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    abstract PdfReader openSource(PdfURLSource source) throws IOException;

    abstract PdfReader openSource(PdfFileSource source) throws IOException;

    abstract PdfReader openSource(PdfStreamSource source) throws IOException;
}
