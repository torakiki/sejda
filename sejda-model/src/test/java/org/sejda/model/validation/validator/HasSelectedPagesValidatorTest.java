/*
 * Created on 02/ott/2011
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

import org.junit.Test;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * @author Andrea Vacondio
 * 
 */
public class HasSelectedPagesValidatorTest {
    private HasSelectedPagesValidator victim = new HasSelectedPagesValidator();
    private ExtractPagesParameters params = mock(ExtractPagesParameters.class);
    private PageRange range = new PageRange(10);

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testHasDefault() {
        when(params.getPredefinedSetOfPages()).thenReturn(PredefinedSetOfPages.EVEN_PAGES);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasTransitions() {
        when(params.getPredefinedSetOfPages()).thenReturn(null);
        when(params.getPageSelection()).thenReturn(Collections.singleton(range));
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasBoth() {
        when(params.getPredefinedSetOfPages()).thenReturn(PredefinedSetOfPages.EVEN_PAGES);
        when(params.getPageSelection()).thenReturn(Collections.singleton(range));
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasNone() {
        when(params.getPredefinedSetOfPages()).thenReturn(null);
        when(params.getPageSelection()).thenReturn(Collections.EMPTY_SET);
        assertFalse(victim.isValid(params, null));
    }
}
