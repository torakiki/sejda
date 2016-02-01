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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterInputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.graphics.color.PDColorSpace;

/**
 * @author Andrea Vacondio
 *
 */
public class ReadOnlyFilteredCOSStreamTest {
    private InputStream stream;
    private COSDictionary dictionary;
    private ReadOnlyFilteredCOSStream victim;

    @Before
    public void setUp() {
        dictionary = new COSDictionary();
        stream = new ByteArrayInputStream(new byte[] { 1, 2 });
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConstructor() {
        new ReadOnlyFilteredCOSStream(new COSDictionary(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readOnlyNullConstructor() throws IOException {
        ReadOnlyFilteredCOSStream.readOnly(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readOnlyJpegImageNullStream() {
        ReadOnlyFilteredCOSStream.readOnlyJpegImage(null, 10, 10, 1, mock(PDColorSpace.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readOnlyJpegImageNullColorSpace() {
        ReadOnlyFilteredCOSStream.readOnlyJpegImage(stream, 10, 10, 1, null);
    }

    @Test(expected = IOException.class)
    public void testGetFilteredLength() throws Exception {
        victim.getFilteredLength();
    }

    @Test(expected = IOException.class)
    public void testGetUnfilteredLength() throws Exception {
        victim.getUnfilteredLength();
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
        stream = mock(InputStream.class);
        victim = new ReadOnlyFilteredCOSStream(dictionary, stream);
        victim.close();
        verify(stream).close();
    }

    @Test
    public void testIndirectLength() {
        assertTrue(victim.indirectLength());
        victim.indirectLength(false);
        assertTrue(victim.indirectLength());
    }

    @Test
    public void embeddedFileIsCompressed() throws Exception {
        victim = ReadOnlyFilteredCOSStream.readOnlyEmbeddedFile(PdfStreamSource.newInstanceNoPassword(stream, "chuck"));
        assertThat(victim.getFilteredStream(), instanceOf(DeflaterInputStream.class));
    }

    @Test(expected = IOException.class)
    public void testGetUnfilteredStream() throws IOException {
        victim.getUnfilteredStream();
    }

    @Test(expected = IOException.class)
    public void testGetUnfilteredSource() throws Exception {
        victim.getUnfilteredSource();
    }

    @Test(expected = SejdaRuntimeException.class)
    public void testCreateFilteredStream() {
        victim.createFilteredStream();
    }

    @Test(expected = SejdaRuntimeException.class)
    public void testCreateFilteredStreamCOSBase() {
        victim.createFilteredStream(COSName.FLATE_DECODE);
    }

    @Test(expected = SejdaRuntimeException.class)
    public void testSetFilters() {
        victim.setFilters(COSName.FLATE_DECODE);
    }

    @Test(expected = SejdaRuntimeException.class)
    public void testCreateUnfilteredStream() {
        victim.createUnfilteredStream();
    }

    @Test
    public void readOnly() throws IOException {
        COSStream existing = spy(new COSStream());
        existing.setItem(COSName.A, COSInteger.THREE);
        victim = ReadOnlyFilteredCOSStream.readOnly(existing);
        assertEquals(COSInteger.THREE, victim.getItem(COSName.A));
        verify(existing).setEncryptor(null);
        verify(existing).getFilteredStream();
    }

    @Test
    public void readOnlyJpegImage() {
        PDColorSpace colorSpace = mock(PDColorSpace.class);
        when(colorSpace.getCOSObject()).thenReturn(COSInteger.TWO);
        victim = ReadOnlyFilteredCOSStream.readOnlyJpegImage(stream, 10, 20, 8, colorSpace);
        assertEquals(COSName.XOBJECT, victim.getItem(COSName.TYPE));
        assertEquals(COSName.IMAGE, victim.getItem(COSName.SUBTYPE));
        assertEquals(COSName.DCT_DECODE, victim.getItem(COSName.FILTER));
        assertEquals(8, victim.getInt(COSName.BITS_PER_COMPONENT));
        assertEquals(20, victim.getInt(COSName.HEIGHT));
        assertEquals(10, victim.getInt(COSName.WIDTH));
        assertEquals(COSInteger.TWO, victim.getItem(COSName.COLORSPACE));
    }
}
