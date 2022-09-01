/*
 * Created on 31 gen 2016
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream.InputStreamSupplier;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.StreamSource;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSObjectKey;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.graphics.color.PDColorSpace;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DeflaterInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 */
public class ReadOnlyFilteredCOSStreamTest {
    private InputStream stream;
    private COSDictionary dictionary;
    private ReadOnlyFilteredCOSStream victim;

    @BeforeEach
    public void setUp() {
        dictionary = new COSDictionary();
        stream = new ByteArrayInputStream(new byte[] { 1, 2 });
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream, 2);
    }

    @Test
    public void nullStream() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReadOnlyFilteredCOSStream(new COSDictionary(), (InputStream) null, 1));
    }

    @Test
    public void nullSupplier() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReadOnlyFilteredCOSStream(new COSDictionary(), (InputStreamSupplier) null, 1));
    }

    @Test
    public void nullConstructorDictionary() {
        assertThrows(IllegalArgumentException.class, () -> new ReadOnlyFilteredCOSStream(null, stream, 1));
    }

    @Test
    public void readOnlyNullConstructor() {
        assertThrows(IllegalArgumentException.class, () -> ReadOnlyFilteredCOSStream.readOnly(null));
    }

    @Test
    public void readOnlyJpegImageNullFile() {
        assertThrows(IllegalArgumentException.class,
                () -> ReadOnlyFilteredCOSStream.readOnlyJpegImage(null, 10, 10, 1, mock(PDColorSpace.class)));
    }

    @Test
    public void readOnlyJpegImageNullColorSpace(@TempDir Path folder) {
        assertThrows(IllegalArgumentException.class,
                () -> ReadOnlyFilteredCOSStream.readOnlyJpegImage(Files.createTempFile(folder, null, null).toFile(), 10,
                        10, 1, null));
    }

    @Test
    public void testGetFilteredLength() throws Exception {
        assertEquals(2, victim.getFilteredLength());
    }

    @Test
    public void testInvalidGetFilteredLength() {
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream, -1);
        assertThrows(IOException.class, () -> victim.getFilteredLength());
    }

    @Test
    public void testGetUnfilteredLength() {
        assertThrows(IOException.class, () -> victim.getUnfilteredLength());
    }

    @Test
    public void testEncryptable() {
        assertTrue(victim.encryptable());
        victim.encryptable(false);
        assertTrue(victim.encryptable());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(victim.isEmpty());
    }

    @Test
    public void testClose() throws Exception {
        InputStream stream = mock(InputStream.class);
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream, 10);
        victim.close();
        assertEquals(-1, victim.getFilteredStream().read());
    }

    @Test
    public void testId() {
        IndirectCOSObjectIdentifier id = new IndirectCOSObjectIdentifier(new COSObjectKey(10, 0), "source");
        dictionary.idIfAbsent(id);
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream, 10);
        assertTrue(victim.hasId());
        assertEquals(id, victim.id());
    }

    @Test
    public void testPutId() {
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream, 10);
        assertFalse(victim.hasId());
        assertNull(victim.id());
        IndirectCOSObjectIdentifier id = new IndirectCOSObjectIdentifier(new COSObjectKey(10, 0), "source");
        dictionary.idIfAbsent(id);
        assertTrue(dictionary.hasId());
        assertEquals(id, dictionary.id());
    }

    @Test
    public void testIndirectLength() {
        assertTrue(victim.indirectLength());
        victim.indirectLength(false);
        assertTrue(victim.indirectLength());
    }

    @Test
    public void embeddedFileIsCompressed() throws Exception {
        victim = ReadOnlyFilteredCOSStream.readOnlyEmbeddedFile(StreamSource.newInstance(stream, "chuck"));
        assertThat(victim.getFilteredStream(), instanceOf(DeflaterInputStream.class));
    }

    @Test
    public void testGetUnfilteredStream() {
        assertThrows(IOException.class, () -> victim.getUnfilteredStream());
    }

    @Test
    public void testGetUnfilteredSource() {
        assertThrows(IOException.class, () -> victim.getUnfilteredSource());
    }

    @Test
    public void testCreateFilteredStream() {
        assertThrows(SejdaRuntimeException.class, () -> victim.createFilteredStream());
    }

    @Test
    public void testCreateFilteredStreamCOSBase() {
        assertThrows(SejdaRuntimeException.class, () -> victim.createFilteredStream(COSName.FLATE_DECODE));
    }

    @Test
    public void testSetFilters() {
        assertThrows(SejdaRuntimeException.class, () -> victim.setFilters(COSName.FLATE_DECODE));
    }

    @Test
    public void testCreateUnfilteredStream() {
        assertThrows(SejdaRuntimeException.class, () -> victim.createUnfilteredStream());
    }

    @Test
    public void readOnly() throws IOException {
        var spy = spy(COSStream.class);
        spy.setItem(COSName.A, COSInteger.THREE);
        victim = ReadOnlyFilteredCOSStream.readOnly(spy);
        assertEquals(COSInteger.THREE, victim.getItem(COSName.A));
        verify(spy).setEncryptor(null);
        verify(spy, never()).getFilteredStream();
        victim.getFilteredStream();
        verify(spy).getFilteredStream();
    }

    @Test
    public void readOnlyJpegImage(@TempDir Path folder) throws IOException {
        PDColorSpace colorSpace = mock(PDColorSpace.class);
        when(colorSpace.getCOSObject()).thenReturn(COSInteger.TWO);
        victim = ReadOnlyFilteredCOSStream.readOnlyJpegImage(Files.createTempFile(folder, null, null).toFile(), 10, 20,
                8, colorSpace);
        assertEquals(COSName.XOBJECT, victim.getItem(COSName.TYPE));
        assertEquals(COSName.IMAGE, victim.getItem(COSName.SUBTYPE));
        assertEquals(COSName.DCT_DECODE, victim.getItem(COSName.FILTER));
        assertEquals(8, victim.getInt(COSName.BITS_PER_COMPONENT));
        assertEquals(20, victim.getInt(COSName.HEIGHT));
        assertEquals(10, victim.getInt(COSName.WIDTH));
        assertEquals(COSInteger.TWO, victim.getItem(COSName.COLORSPACE));
    }

}
