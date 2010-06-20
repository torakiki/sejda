/*
 * Created on 31/mag/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.task.itext.component;

import java.io.FileInputStream;
import java.io.IOException;

import org.sejda.core.exception.TaskIOException;
import org.sejda.core.exception.TaskWrongPasswordException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;

import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

/**
 * Component responsible for handling operations related to a {@link PdfReader} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfReaderHandler {
    /**
     * Open a {@link PdfReader} from the input {@link PdfSource}
     * 
     * @param source
     *            where the {@link PdfReader} will be opened.
     * @param forceStream
     *            if true and source is a {@link PdfFileSource}, forces the {@link PdfReader} to be opened from a {@link FileInputStream}
     * @return the opened {@link PdfReader}
     * @throws TaskIOException
     *             if an error occur during the reader creation.
     */
    public PdfReader openReader(PdfSource source, boolean forceStream) throws TaskIOException {
        PdfReader reader = null;
        try {
            switch (source.getSourceType()) {
            case FILE_SOURCE:
                if (forceStream) {
                    reader = openReaderFromFileAsStream((PdfFileSource) source);
                }
                reader = openReaderFromFile((PdfFileSource) source);
                break;
            case STREAM_SOURCE:
                reader = openReaderFromStream((PdfStreamSource) source);
                break;
            case URL_SOURCE:
                reader = openReaderFromURL((PdfURLSource) source);
                break;
            default:
                throw new TaskIOException("Unable to identify the input pdf source.");
            }
        } catch (BadPasswordException bpe) {
            throw new TaskWrongPasswordException("Unable to open the document due to a wrong password.", bpe);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    /**
     * Open a {@link PdfReader} from the input {@link PdfSource}
     * 
     * @param source
     *            where the {@link PdfReader} will be opened.
     * @return he opened {@link PdfReader}
     * @throws TaskIOException
     *             if an error occur during the reader creation
     */
    public PdfReader openReader(PdfSource source) throws TaskIOException {
        return openReader(source, false);
    }

    private PdfReader openReaderFromStream(PdfStreamSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getStream()), source.getPasswordBytes());
    }

    private PdfReader openReaderFromFile(PdfFileSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getFile().getAbsolutePath()), source.getPasswordBytes());
    }

    private PdfReader openReaderFromURL(PdfURLSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getUrl()), source.getPasswordBytes());
    }

    private PdfReader openReaderFromFileAsStream(PdfFileSource source) throws IOException {
        return new PdfReader(new FileInputStream(source.getFile()), source.getPasswordBytes());
    }

    /**
     * Closes the input {@link PdfReader}
     * 
     * @param pdfReader
     */
    public void closePdfReader(PdfReader pdfReader) {
        if (pdfReader != null) {
            pdfReader.close();
        }
    }
}
