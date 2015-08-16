/*
 * Created on 03/jul/2011
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;

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
     * @return The number of bytes that have been copied. Unfortunately is not really accurate because iText underlying uses a BufferedOutputStream that is written only when the
     *         buffer size is reached so it can happen that you write some pages but this method always return the same number because the bytes didn't actually passed yet through
     *         the {@link CountingOutputStream}.
     */
    public long getByteCount() {
        return outputStream.getByteCount();
    }

    /**
     * Tries to estimate the size of the output document once added a new page. This estimation can be little accurate when the number of written pages is low.
     * 
     * @return an estimation of the size of the output document if a new pages is added.
     */
    public long getEstimatedSizeAfterNextPage() {
        return getByteCount() + (getByteCount() / getNumberOfCopiedPages());
    }
}
