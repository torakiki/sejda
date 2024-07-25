/*
 * Created on 29/mag/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.input;

import org.sejda.io.SeekableSource;
import org.sejda.io.SeekableSources;
import org.sejda.model.exception.TaskIOException;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link AbstractPdfSource} from a {@link InputStream}
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfStreamSource extends AbstractPdfSource<InputStream> {

    @NotNull
    private final InputStream stream;

    private PdfStreamSource(InputStream stream, String name, String password) {
        super(name, password);
        this.stream = stream;
    }

    @Override
    public InputStream getSource() {
        return stream;
    }

    @Override
    public <T> T open(PdfSourceOpener<T> opener) throws TaskIOException {
        return opener.open(this);
    }

    @Override
    public SeekableSource initializeSeekableSource() throws IOException {
        return SeekableSources.onTempFileSeekableSourceFrom(getEncryptionAtRestPolicy().decrypt(stream), getName());
    }

    /**
     * Creates a new instance of the pdf source where a password is NOT required to open the source.
     * 
     * @param stream
     *            input pdf stream
     * @param name
     * @return a newly created instance
     */
    public static PdfStreamSource newInstanceNoPassword(InputStream stream, String name) {
        return PdfStreamSource.newInstanceWithPassword(stream, name, null);
    }

    /**
     * Creates a new instance of the pdf source where a password is required to open the source.
     * 
     * @param stream
     *            input pdf stream
     * @param name
     * @param password
     * @return a newly created instance
     * @throws IllegalArgumentException
     *             if the input stream or the input name are blank.
     */
    public static PdfStreamSource newInstanceWithPassword(InputStream stream, String name, String password) {
        if (stream == null) {
            throw new IllegalArgumentException("A not null stream instance and a not blank name are expected.");
        }
        return new PdfStreamSource(stream, name, password);
    }
}
