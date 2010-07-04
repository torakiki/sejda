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

import java.io.BufferedInputStream;
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
     *            if true the {@link PdfReader} is opened from an {@link InputStream}, if false it's openend from a {@link RandomAccessFileOrArray}
     * @return the opened {@link PdfReader}
     * @throws TaskIOException
     *             if an error occur during the reader creation.
     */
    public PdfReader openReader(PdfSource source, boolean forceStream) throws TaskIOException {
        PdfReader reader = null;
        try {
            switch (source.getSourceType()) {
            case FILE_SOURCE:
                reader = openReaderFromFile((PdfFileSource) source, forceStream);
                break;
            case STREAM_SOURCE:
                reader = openReaderFromStream((PdfStreamSource) source, forceStream);
                break;
            case URL_SOURCE:
                reader = openReaderFromURL((PdfURLSource) source, forceStream);
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

    private PdfReader openReaderFromStream(PdfStreamSource source, boolean forceStream) throws IOException {
        if (forceStream) {
            return new PdfReader(new BufferedInputStream(source.getStream()), source.getPasswordBytes());
        } else {
            return new PdfReader(new RandomAccessFileOrArray(source.getStream()), source.getPasswordBytes());
        }
    }

    private PdfReader openReaderFromFile(PdfFileSource source, boolean forceStream) throws IOException {
        if (forceStream) {
            return new PdfReader(new BufferedInputStream(new FileInputStream(source.getFile())), source
                    .getPasswordBytes());
        } else {
            return new PdfReader(new RandomAccessFileOrArray(source.getFile().getAbsolutePath()), source
                    .getPasswordBytes());
        }
    }

    private PdfReader openReaderFromURL(PdfURLSource source, boolean forceStream) throws IOException {
        if (forceStream) {
            return new PdfReader(new BufferedInputStream(source.getUrl().openStream()), source.getPasswordBytes());
        } else {
            return new PdfReader(new RandomAccessFileOrArray(source.getUrl()), source.getPasswordBytes());
        }
    }

}
