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
package org.sejda.core.manipulation.model.pdf.page;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * Test unit for the Bounds class
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageRangeTest {

    @Test
    public void testIntersect() {
        PageRange base = new PageRange(0, 10);
        PageRange noIntersection = new PageRange(11, 12);
        PageRange intersecion = new PageRange(5, 15);
        PageRange included = new PageRange(2, 5);
        assertTrue(base.intersects(intersecion));
        assertTrue(base.intersects(included));
        assertFalse(base.intersects(noIntersection));
    }

    @Test
    public void testEquals() {
        PageRange eq1 = new PageRange(1, 10);
        PageRange eq2 = new PageRange(1, 10);
        PageRange eq3 = new PageRange(1, 10);
        PageRange diff = new PageRange(1, 9);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testUnbounded() {
        PageRange victim = new PageRange(10);
        assertTrue(victim.isUnbounded());
    }
}
