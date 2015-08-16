/*
 * Created on 03/set/2011
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

/**
 * @author Andrea Vacondio
 * 
 */
public class HasTransitionsValidatorTest {

    private HasTransitionsValidator victim = new HasTransitionsValidator();
    private SetPagesTransitionParameters params = mock(SetPagesTransitionParameters.class);
    private PdfPageTransition mockTransition = PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL,
            1, 1);

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testHasDefault() {
        when(params.getDefaultTransition()).thenReturn(mockTransition);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasTransitions() {
        when(params.getDefaultTransition()).thenReturn(null);
        when(params.getTransitions()).thenReturn(Collections.singletonMap(Integer.MAX_VALUE, mockTransition));
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasBoth() {
        when(params.getDefaultTransition()).thenReturn(mockTransition);
        when(params.getTransitions()).thenReturn(Collections.singletonMap(Integer.MAX_VALUE, mockTransition));
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasNone() {
        when(params.getDefaultTransition()).thenReturn(null);
        Map<Integer, PdfPageTransition> empty = Collections.emptyMap();
        when(params.getTransitions()).thenReturn(empty);
        assertFalse(victim.isValid(params, null));
    }
}
