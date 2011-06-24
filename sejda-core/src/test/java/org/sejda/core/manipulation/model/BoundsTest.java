/*
 * Created on 18/set/2010
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
package org.sejda.core.manipulation.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test unit for the Bounds class
 * 
 * @author Andrea Vacondio
 * 
 */
public class BoundsTest {

    @Test
    public void testIntersect() {
        Bounds base = new Bounds(0, 10);
        Bounds noIntersection = new Bounds(11, 12);
        Bounds intersecion = new Bounds(5, 15);
        Bounds included = new Bounds(2, 5);
        assertTrue(base.intersects(intersecion));
        assertTrue(base.intersects(included));
        assertFalse(base.intersects(noIntersection));
    }
}
