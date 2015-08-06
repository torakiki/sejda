/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.input.PdfMixInput.PdfMixInputProcessStatus;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfMixInputTest {

    private PdfSource<?> source;

    @Before
    public void setUp() {
        source = mock(PdfSource.class);
    }

    @Test
    public void testStatus() {
        PdfMixInput victim = new PdfMixInput(source);
        PdfMixInputProcessStatus status = victim.newProcessingStatus(10);
        assertTrue(status.hasNextPage());
        assertEquals(1, status.nextPage());
    }

    @Test
    public void testStatusReverse() {
        PdfMixInput victim = new PdfMixInput(source, true, 1);
        PdfMixInputProcessStatus status = victim.newProcessingStatus(10);
        assertTrue(status.hasNextPage());
        assertEquals(10, status.nextPage());
    }

    @Test
    public void testStatusNoNextPage() {
        PdfMixInput victim = new PdfMixInput(source);
        PdfMixInputProcessStatus status = victim.newProcessingStatus(2);
        assertTrue(status.hasNextPage());
        assertEquals(1, status.nextPage());
        assertTrue(status.hasNextPage());
        assertEquals(2, status.nextPage());
        assertFalse(status.hasNextPage());
    }
}
