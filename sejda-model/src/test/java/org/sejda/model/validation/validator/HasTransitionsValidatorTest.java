/*
 * Created on 03/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
