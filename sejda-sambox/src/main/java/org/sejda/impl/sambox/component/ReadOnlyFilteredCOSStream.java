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

import org.sejda.io.SeekableSource;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.FileSource;
import org.sejda.model.input.Source;
import org.sejda.model.input.SourceDispatcher;
import org.sejda.model.input.StreamSource;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.graphics.color.PDColorSpace;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.zip.DeflaterInputStream;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.requireIOCondition;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * A read only, filtered, encryptable, indirect reference length {@link COSStream} whose purpose is to be read by the PDF writer during the write process. This can allow to create
 * streams from File input streams and predefine the expected dictionary without having to read anything into memory.
 *
 * @author Andrea Vacondio
 */
public class ReadOnlyFilteredCOSStream extends COSStream {
    private InputStreamSupplier<InputStream> supplier;
    private final long length;
    private final COSDictionary wrapped;

    ReadOnlyFilteredCOSStream(COSDictionary existingDictionary, InputStream supplier, long length) {
        this(existingDictionary, () -> supplier, length);
        requireNotNullArg(supplier, "input stream cannot be null");
    }

    public ReadOnlyFilteredCOSStream(COSDictionary existingDictionary, InputStreamSupplier<InputStream> supplier,
            long length) {
        super(ofNullable(existingDictionary).orElseThrow(
                () -> new IllegalArgumentException("wrapped dictionary cannot be null")));
        requireNotNullArg(supplier, "input stream provider cannot be null");
        this.supplier = supplier;
        this.length = length;
        this.wrapped = existingDictionary;
    }

    @Override
    protected InputStream doGetFilteredStream() throws IOException {
        return supplier.get();
    }

    @Override
    public long getFilteredLength() throws IOException {
        requireIOCondition(length >= 0, "Filtered length cannot be requested");
        return length;
    }

    @Override
    public long getUnfilteredLength() throws IOException {
        throw new IOException("Unfiltered length cannot be requested");
    }

    @Override
    public InputStream getUnfilteredStream() throws IOException {
        throw new IOException("getUnfilteredStream  cannot be requested");
    }

    @Override
    public SeekableSource getUnfilteredSource() throws IOException {
        throw new IOException("getUnfilteredSource  cannot be requested");
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
    public boolean addCompression() {
        return false;
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
    public IndirectCOSObjectIdentifier id() {
        return wrapped.id();
    }

    @Override
    public void idIfAbsent(IndirectCOSObjectIdentifier id) {
        wrapped.idIfAbsent(id);
    }

    @Override
    public void close() throws IOException {
        //consistent with COSStream that once closed has COSStream::getFilteredStream returning an empty ByteArrayInputStream
        this.supplier = () -> new ByteArrayInputStream(new byte[0]);
    }

    /**
     * a {@link ReadOnlyFilteredCOSStream} from an existing {@link COSStream}
     * 
     * @param existing
     * @return the created instance
     * @throws IOException
     */
    public static ReadOnlyFilteredCOSStream readOnly(COSStream existing) throws IOException {
        requireNotNullArg(existing, "input stream cannot be null");
        // let's make sure we get the unencrypted and filtered
        existing.setEncryptor(null);
        return new ReadOnlyFilteredCOSStream(existing, existing::getFilteredStream, existing.getFilteredLength());
    }

    /**
     * a {@link ReadOnlyFilteredCOSStream} that represents an xobject JPEG image
     * 
     * @param imageFile
     *            the image file
     * @param width
     * @param height
     * @param bitsPerComponent
     * @param colorSpace
     * @return the created instance
     * @throws FileNotFoundException
     */
    public static ReadOnlyFilteredCOSStream readOnlyJpegImage(File imageFile, int width, int height,
            int bitsPerComponent, PDColorSpace colorSpace) {
        requireNotNullArg(imageFile, "input file cannot be null");
        requireNotNullArg(colorSpace, "color space cannot be null");
        COSDictionary dictionary = new COSDictionary();
        dictionary.setItem(COSName.TYPE, COSName.XOBJECT);
        dictionary.setItem(COSName.SUBTYPE, COSName.IMAGE);
        dictionary.setItem(COSName.FILTER, COSName.DCT_DECODE);
        dictionary.setInt(COSName.BITS_PER_COMPONENT, bitsPerComponent);
        dictionary.setInt(COSName.HEIGHT, height);
        dictionary.setInt(COSName.WIDTH, width);
        of(colorSpace).map(PDColorSpace::getCOSObject).ifPresent(cs -> dictionary.setItem(COSName.COLORSPACE, cs));
        return new ReadOnlyFilteredCOSStream(dictionary, () -> new FileInputStream(imageFile), imageFile.length());
    }

    /**
     * a {@link ReadOnlyFilteredCOSStream} representing an embedded file stream
     * 
     * @param source
     * @return the created instance
     * @throws TaskIOException
     */
    public static final ReadOnlyFilteredCOSStream readOnlyEmbeddedFile(Source<?> source) throws TaskIOException {
        COSDictionary dictionary = new COSDictionary();
        dictionary.setItem(COSName.FILTER, COSName.FLATE_DECODE);
        return source.dispatch(new SourceDispatcher<ReadOnlyFilteredCOSStream>() {

            @Override
            public ReadOnlyFilteredCOSStream dispatch(FileSource source) throws TaskIOException {
                try {
                    ReadOnlyFilteredCOSStream retVal = new ReadOnlyFilteredCOSStream(dictionary,
                            new DeflaterInputStream(new FileInputStream(source.getSource())), -1);
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
            public ReadOnlyFilteredCOSStream dispatch(StreamSource source) {
                return new ReadOnlyFilteredCOSStream(dictionary, new DeflaterInputStream(source.getSource()), -1);
            }
        });
    }

    @FunctionalInterface
    public interface InputStreamSupplier<T extends InputStream> {
        T get() throws IOException;
    }
}
