package org.sejda.core.manipulation.model.task.itext.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

import com.lowagie.text.pdf.PdfReader;

/**
 * Pdf copier implementation using a {@link File} as output. This implementation counts the number of bytes written to the output file.
 * 
 * @author Andrea Vacondio
 * 
 */
public class CountingPdfCopier extends AbstractPdfCopier {

    private CountingOutputStream outputStream;

    /**
     * Creates a copier that writes to the given output file counting the written bytes.
     * 
     * @param reader
     * @param outputFile
     * @param version
     *            version for the created pdf copy, if null the version number is taken from the input PdfReader.
     * @throws TaskException
     *             if the file is not found or an error occur opening the underlying copier.
     */
    public CountingPdfCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        try {
            outputStream = new CountingOutputStream(new FileOutputStream(outputFile));
            open(reader, outputStream, version);
        } catch (FileNotFoundException e) {
            throw new TaskException(String.format("Unable to find the output file %s", outputFile.getPath()), e);
        }
    }

    @Override
    public void close() {
        super.close();
        IOUtils.closeQuietly(outputStream);
    }

    /**
     * @return The number of bytes that have been copied.
     */
    public long getByteCount() {
        return outputStream.getByteCount();
    }

}
