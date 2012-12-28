/*
 * Created on 27/dic/2012
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

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.validation.constraint.NotAllowed;

/**
 * @author Andrea Vacondio
 * 
 */
public class NotAllowedValidatorTest {

    private NotAllowedValidator victim = new NotAllowedValidator();
    private NotAllowed annotation;

    @Before
    public void setUp() {
        annotation = mock(NotAllowed.class);
        when(annotation.disallow()).thenReturn(new PredefinedSetOfPages[] { PredefinedSetOfPages.ALL_PAGES });
        victim.initialize(annotation);
    }

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testNoDisallow() {
        when(annotation.disallow()).thenReturn(new PredefinedSetOfPages[] {});
        victim.initialize(annotation);
        assertTrue(victim.isValid(PredefinedSetOfPages.ALL_PAGES, null));
    }

    @Test
    public void testDisallow() {
        assertFalse(victim.isValid(PredefinedSetOfPages.ALL_PAGES, null));
    }

}
