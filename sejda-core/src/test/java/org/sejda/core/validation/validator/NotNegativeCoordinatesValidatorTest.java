/*
 * Created on 10/set/2011
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
package org.sejda.core.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.manipulation.model.RectangularBox;

/**
 * @author Andrea Vacondio
 * 
 */
public class NotNegativeCoordinatesValidatorTest {

    private NotNegativeCoordinatesValidator victim = new NotNegativeCoordinatesValidator();
    private RectangularBox box;

    @Before
    public void setUp() {
        box = RectangularBox.newInstance(0, 0, 10, 10);
    }

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testTopNegative() {
        TestUtils.setProperty(box, "top", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testBottomNegative() {
        TestUtils.setProperty(box, "bottom", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testLeftNegative() {
        TestUtils.setProperty(box, "left", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testRightNegative() {
        TestUtils.setProperty(box, "right", -1);
        assertFalse(victim.isValid(box, null));
    }

    @Test
    public void testValid() {
        assertTrue(victim.isValid(box, null));
    }
}
