/*
 * Created on 27/dic/2012
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
