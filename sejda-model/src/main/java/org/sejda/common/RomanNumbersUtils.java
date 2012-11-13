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

/**
 * Utility providing Roman numbers conversion functionalities
 * 
 * @author Andrea Vacondio
 * @see <a href="http://rosettacode.org/wiki/Roman_numerals/Encode#Java">Roman numerals encode</a>
 */
public final class RomanNumbersUtils {

    private RomanNumbersUtils() {
        // hide
    }

    /**
     * @param toConvert
     * @return a a String representation in roman numbers of the input long
     */
    public static String toRoman(long toConvert) {

        if (toConvert <= 0) {
            throw new IllegalArgumentException();
        }

        long n = toConvert;
        StringBuilder buf = new StringBuilder();
        final Numeral[] values = Numeral.values();
        for (int i = values.length - 1; i >= 0; i--) {
            while (n >= values[i].weight) {
                buf.append(values[i]);
                n -= values[i].weight;
            }
        }
        return buf.toString();
    }

    /**
     * Enum providing equivalence between roman numbers and integers
     * 
     * @author Andrea Vacondio
     * 
     */
    private enum Numeral {
        I(1),
        IV(4),
        V(5),
        IX(9),
        X(10),
        XL(40),
        L(50),
        XC(90),
        C(100),
        CD(400),
        D(500),
        CM(900),
        M(1000);

        private int weight;

        Numeral(int weight) {
            this.weight = weight;
        }
    }
}
