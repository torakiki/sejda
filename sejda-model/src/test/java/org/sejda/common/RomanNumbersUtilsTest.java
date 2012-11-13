/*
 * Created on 13/nov/2012
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
package org.sejda.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class RomanNumbersUtilsTest {

    @Test
    public void testToRomans() {
        assertEquals("MCMX", RomanNumbersUtils.toRoman(1910));
        assertEquals("MCMLIV", RomanNumbersUtils.toRoman(1954));
        assertEquals("MCMXC", RomanNumbersUtils.toRoman(1990));
        assertEquals("C", RomanNumbersUtils.toRoman(100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailingNegative() {
        RomanNumbersUtils.toRoman(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailingZero() {
        RomanNumbersUtils.toRoman(0);
    }

}
