/*
 * Created on 29/mag/2010
 *
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
package org.sejda.core.manipulation.model.rotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.core.TestUtils;

/**
 * Test unit for the page rotation
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageRotationTest {

    @Test
    public void testAccept() {
        PageRotation victim = PageRotation.createSinglePageRotation(2, Rotation.DEGREES_180);
        assertTrue(victim.accept(2));
        assertFalse(victim.accept(1));

        victim = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.EVEN_PAGES);
        assertTrue(victim.accept(2));
        assertFalse(victim.accept(1));

        victim = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.ODD_PAGES);
        assertTrue(victim.accept(1));
        assertFalse(victim.accept(2));

        victim = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.ALL_PAGES);
        assertTrue(victim.accept(2));
        assertTrue(victim.accept(1));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalNegativePageNumber() {
        PageRotation.createSinglePageRotation(-1, Rotation.DEGREES_180);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalRotationType() {
        PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.SINGLE_PAGE);
    }

    @Test
    public void testEqualsAndHashcode() {
        PageRotation victim1 = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.ALL_PAGES);
        PageRotation victim2 = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.ALL_PAGES);
        PageRotation victim3 = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_270, RotationType.ALL_PAGES);
        PageRotation victim4 = PageRotation.createMultiplePagesRotation(Rotation.DEGREES_180, RotationType.ALL_PAGES);
        TestUtils.testEqualsAndHashCodes(victim1, victim2, victim3, victim4);
    }

}
