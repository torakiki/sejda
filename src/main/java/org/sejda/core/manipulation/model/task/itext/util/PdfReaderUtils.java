/*
 * Created on 31/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.task.itext.util;

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
 * Utility responsible for handling operations related to a {@link PdfReader} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfReaderUtils {

    private PdfReaderUtils() {
        // utility
    }

    /**
     * Open a {@link PdfReader} from the input {@link PdfSource}
     * 
     * @param source
     *            where the {@link PdfReader} will be opened.
     * @param forceStream
     *            if true the {@link PdfReader} is opened from an InputStream, if false it's opened from a {@link RandomAccessFileOrArray}
     * @return the opened {@link PdfReader}
     * @throws TaskIOException
     *             if an error occur during the reader creation.
     */
    public static PdfReader openReader(PdfSource source, boolean forceStream) throws TaskIOException {
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
    public static PdfReader openReader(PdfSource source) throws TaskIOException {
        return openReader(source, false);
    }

    private static PdfReader openReaderFromStream(PdfStreamSource source, boolean forceStream) throws IOException {
        if (forceStream) {
            return new PdfReader(new BufferedInputStream(source.getStream()), source.getPasswordBytes());
        } else {
            return new PdfReader(new RandomAccessFileOrArray(source.getStream()), source.getPasswordBytes());
        }
    }

    private static PdfReader openReaderFromFile(PdfFileSource source, boolean forceStream) throws IOException {
        if (forceStream) {
            return new PdfReader(new BufferedInputStream(new FileInputStream(source.getFile())), source
                    .getPasswordBytes());
        } else {
            return new PdfReader(new RandomAccessFileOrArray(source.getFile().getAbsolutePath()), source
                    .getPasswordBytes());
        }
    }

    private static PdfReader openReaderFromURL(PdfURLSource source, boolean forceStream) throws IOException {
        if (forceStream) {
            return new PdfReader(new BufferedInputStream(source.getUrl().openStream()), source.getPasswordBytes());
        } else {
            return new PdfReader(new RandomAccessFileOrArray(source.getUrl()), source.getPasswordBytes());
        }
    }

}
