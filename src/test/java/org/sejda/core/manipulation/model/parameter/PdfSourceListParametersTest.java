/*
 * Created on 17/set/2010
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
package org.sejda.core.manipulation.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.sejda.core.manipulation.model.input.PdfSource;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfSourceListParametersTest {

    @Test
    public void testAdd() {
        PdfSourceListParameters victim = new PdfSourceListParameters();
        PdfSource source = mock(PdfSource.class);
        victim.addSource(source);
        assertEquals(1, victim.getSourceList().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableList() {
        PdfSourceListParameters victim = new PdfSourceListParameters();
        PdfSource source = mock(PdfSource.class);
        victim.addSource(source);
        victim.getSourceList().clear();
    }

    @Test
    public void testClear() {
        PdfSourceListParameters victim = new PdfSourceListParameters();
        PdfSource source = mock(PdfSource.class);
        victim.addSource(source);
        assertEquals(1, victim.getSourceList().size());
        victim.clearSourceList();
        assertEquals(0, victim.getSourceList().size());
    }
}
