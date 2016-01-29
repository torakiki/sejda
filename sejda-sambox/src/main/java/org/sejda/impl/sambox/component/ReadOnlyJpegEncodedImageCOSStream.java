/*
 * Created on 28 gen 2016
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.sejda.io.SeekableSource;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.graphics.color.PDColorSpace;
import org.sejda.util.IOUtils;

/**
 * @author Andrea Vacondio
 *
 */
public class ReadOnlyJpegEncodedImageCOSStream extends COSStream {
    private InputStream stream;

    public ReadOnlyJpegEncodedImageCOSStream(InputStream stream, int width, int height, int bitsPerComponent,
            PDColorSpace colorSpace) {
        requireNotNullArg(stream, "input stream cannot be null");
        requireNotNullArg(colorSpace, "color space cannot be null");
        this.stream = stream;
        setItem(COSName.FILTER, COSName.DCT_DECODE);
        setInt(COSName.BITS_PER_COMPONENT, bitsPerComponent);
        setInt(COSName.HEIGHT, height);
        setInt(COSName.WIDTH, width);
        Optional.ofNullable(colorSpace).map(PDColorSpace::getCOSObject)
                .ifPresent(cs -> setItem(COSName.COLORSPACE, cs));
    }

    @Override
    protected InputStream doGetFilteredStream() {
        return stream;
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
    public InputStream getUnfilteredStream() throws IOException {
        throw new IOException("getUnfilteredStream  cannot be requested");
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
}
