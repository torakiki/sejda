/*
 * Created on 25 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.sejda.io.SeekableSources.inMemorySeekableSourceFrom;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.zip.DeflaterInputStream;

import org.sejda.io.SeekableSource;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.util.IOUtils;

/**
 * A read only {@link COSStream} that reads from the underlying {@link InputStream}, is always compressed and has a length written as indirect object
 * 
 * @author Andrea Vacondio
 *
 */
public final class ReadOnlyCompressedCOSStream extends COSStream {
    private InputStream stream;

    public ReadOnlyCompressedCOSStream(InputStream stream) {
        requireNotNullArg(stream, "input stream cannot be null");
        this.stream = stream;
        setItem(COSName.FILTER, COSName.FLATE_DECODE);
    }

    @Override
    protected InputStream doGetFilteredStream() {
        return new DeflaterInputStream(stream);
    }

    @Override
    public long getFilteredLength() throws IOException {
        throw new IOException("Embedded files filtered length cannot be requested");
    }

    @Override
    public long getUnfilteredLength() throws IOException {
        throw new IOException("Embedded files unfiltered length cannot be requested");
    }

    @Override
    public InputStream getUnfilteredStream() {
        return stream;
    }

    @Override
    public SeekableSource getUnfilteredSource() throws IOException {
        return inMemorySeekableSourceFrom(stream);
    }

    @Override
    public OutputStream createFilteredStream() {
        throw new SejdaRuntimeException("createFilteredStream cannot be called on this stream");
    }

    @Override
    public OutputStream createFilteredStream(COSBase filters) {
        throw new SejdaRuntimeException("createFilteredStream cannot be called on this stream");
    }

    @Override
    public void setFilters(COSBase filters) {
        throw new SejdaRuntimeException("setFilters cannot be called on this stream");
    }

    @Override
    public void addCompression() {
        // do nothing, it's already supposed to be compressed
    }

    @Override
    public boolean encryptable() {
        return true;
    }

    @Override
    public void encryptable(boolean encryptable) {
        // do nothing, it can be encrypted
    }

    @Override
    public OutputStream createUnfilteredStream() {
        throw new SejdaRuntimeException("createUnfilteredStream cannot be called on this stream");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean indirectLength() {
        return true;
    }

    @Override
    public void indirectLength(boolean indirectLength) {
        // do nothing, it's always written as indirect
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(stream);
    }

    /**
     * Factory method to create a {@link ReadOnlyCompressedCOSStream} from a {@link PdfSource}
     * 
     * @param source
     * @return
     * @throws TaskIOException
     */
    public static final ReadOnlyCompressedCOSStream fromSource(PdfSource<?> source) throws TaskIOException {
        return source.open(new PdfSourceOpener<ReadOnlyCompressedCOSStream>() {

            @Override
            public ReadOnlyCompressedCOSStream open(PdfURLSource source) throws TaskIOException {
                try {
                    return new ReadOnlyCompressedCOSStream(source.getSource().openStream());
                } catch (IOException e) {
                    throw new TaskIOException(e);
                }
            }

            @Override
            public ReadOnlyCompressedCOSStream open(PdfFileSource source) throws TaskIOException {
                try {
                    ReadOnlyCompressedCOSStream retVal = new ReadOnlyCompressedCOSStream(new FileInputStream(source.getSource()));
                    retVal.setEmbeddedInt(COSName.PARAMS.getName(), COSName.SIZE, source.getSource().length());
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(source.getSource().lastModified());
                    retVal.setEmbeddedDate(COSName.PARAMS.getName(), COSName.MOD_DATE, calendar);
                    return retVal;
                } catch (FileNotFoundException e) {
                    throw new TaskIOException(e);
                }
            }

            @Override
            public ReadOnlyCompressedCOSStream open(PdfStreamSource source) {
                return new ReadOnlyCompressedCOSStream(source.getSource());
            }
        });
    }
}
