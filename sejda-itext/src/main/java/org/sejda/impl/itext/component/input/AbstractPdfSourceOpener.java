/*
 * Created on 16/jul/2011
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
package org.sejda.impl.itext.component.input;

import java.io.IOException;
import java.lang.reflect.Field;

import org.sejda.core.Sejda;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskWrongPasswordException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract implementation for a PdfSourceOpener returning a {@link PdfReader}. Subclasses have to provide implementation of the actual source open creating a {@link PdfReader}.
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class AbstractPdfSourceOpener implements PdfSourceOpener<PdfReader> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdfSourceOpener.class);

    public PdfReader open(PdfURLSource source) throws TaskIOException {
        PdfReader reader;
        try {
            reader = makeUnethicalIfRequired(openSource(source));
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
            reader = makeUnethicalIfRequired(openSource(source));
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
            reader = makeUnethicalIfRequired(openSource(source));
        } catch (BadPasswordException bpe) {
            throw new TaskWrongPasswordException("Unable to open the document due to a wrong password.", bpe);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    private PdfReader makeUnethicalIfRequired(PdfReader reader) {
        if (Boolean.getBoolean(Sejda.UNETHICAL_READ_PROPERTY_NAME) && !reader.isOpenedWithFullPermissions()) {
            Field field;
            try {
                field = PdfReader.class.getDeclaredField("encrypted");
                field.setAccessible(true);
                field.setBoolean(reader, false);
            } catch (NoSuchFieldException e) {
                // this should not happen
                throw new SejdaRuntimeException("Error making PdfReader unethical", e);
            } catch (IllegalAccessException e) {
                LOG.warn("Unable to make the reader unethical", e);
            }
        }
        return reader;
    }

    abstract PdfReader openSource(PdfURLSource source) throws IOException;

    abstract PdfReader openSource(PdfFileSource source) throws IOException;

    abstract PdfReader openSource(PdfStreamSource source) throws IOException;
}
