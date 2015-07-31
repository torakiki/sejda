/*
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.pdf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.sejda.model.pdf.TextStampPattern.dateNow;

public class TextStampPatternTest {

    @Test
    public void testPageNumbers() {
        String result = new TextStampPattern().withPage(3, 17).build("Page [PAGE_OF_TOTAL] - [PAGE_ROMAN] - [PAGE_ARABIC]");
        assertEquals("Page 3 of 17 - III - 3", result);
    }

    @Test
    public void testDate() {
        String result = new TextStampPattern().build("FooBar [DATE]");
        String expected = "FooBar " + dateNow();
        assertEquals(expected, result);
    }

    @Test
    public void testBatesNumbering() {
        String result = new TextStampPattern().withPage(3, 17).withBatesSequence("000002").build("Case XYZ [BATES_NUMBER]");
        assertEquals("Case XYZ 000002", result);
    }
}
