/*
 * Created on 17/set/2010
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
package org.sejda.model.parameter.base;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.TaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class MultiplePdfSourceParametersTest {

    private MultiplePdfSourceParameters victim;

    @Before
    public void setUp() {
        victim = new MultiplePdfSourceParameters() {

            @Override
            public TaskOutput getOutput() {
                return null;
            }

        };
    }

    @Test
    public void testAdd() {

        PdfSource<?> source = mock(PdfSource.class);
        victim.addSource(source);
        assertEquals(1, victim.getSourceList().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableList() {
        PdfSource<?> source = mock(PdfSource.class);
        victim.addSource(source);
        victim.getSourceList().clear();
    }
}
