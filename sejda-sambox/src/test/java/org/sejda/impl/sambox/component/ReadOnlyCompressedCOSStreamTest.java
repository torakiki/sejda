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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterInputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.sambox.cos.COSName;

/**
 * @author Andrea Vacondio
 *
 */
public class ReadOnlyCompressedCOSStreamTest {
    private InputStream stream;
    private ReadOnlyCompressedCOSStream victim;

    @Before
    public void createReadOnlyCOSStream() {
        stream = new ByteArrayInputStream(new byte[] { 1, 2 });
        victim = new ReadOnlyCompressedCOSStream(stream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonNullConstructor() throws Exception {
        new ReadOnlyCompressedCOSStream(null);
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
        victim = new ReadOnlyCompressedCOSStream(stream);
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
    public void testDoGetFilteredStream() throws Exception {
        assertThat(victim.getFilteredStream(), instanceOf(DeflaterInputStream.class));
    }

    @Test
    public void testGetUnfilteredStream() {
        assertEquals(stream, victim.getUnfilteredStream());
    }

    @Test
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

}
