/*
 * Created on 09/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.outline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class OutlineGoToPageDestinationsTest {

    @Test
    public void testAdd() {
        OutlineGoToPageDestinations victim = new OutlineGoToPageDestinations();
        assertTrue(victim.isEmpty());
        victim.addPage(1, "Title1");
        assertFalse(victim.isEmpty());
        assertEquals(1, victim.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNull() {
        OutlineGoToPageDestinations victim = new OutlineGoToPageDestinations();
        victim.addPage(null, "Title1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegative() {
        OutlineGoToPageDestinations victim = new OutlineGoToPageDestinations();
        victim.addPage(-3, "Title1");
    }

    @Test
    public void testGetPages() {
        OutlineGoToPageDestinations victim = new OutlineGoToPageDestinations();
        victim.addPage(1, "Title1");
        victim.addPage(2, "Title2");
        assertEquals(2, victim.getPages().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiablePagesSet() {
        OutlineGoToPageDestinations victim = new OutlineGoToPageDestinations();
        victim.addPage(1, "Title1");
        victim.addPage(2, "Title2");
        victim.getPages().add(3);
    }

    public void testGetTitle() {
        OutlineGoToPageDestinations victim = new OutlineGoToPageDestinations();
        victim.addPage(1, "Title1");
        assertEquals("Title1", victim.getTitle(1));
    }
}
