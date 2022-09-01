/*
 * Created on 13/nov/2012
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
package org.sejda.model.util;

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

        if (toConvert < 0) {
            throw new IllegalArgumentException();
        }
        
        if (toConvert == 0) {
            return "nulla";
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

        private final int weight;

        Numeral(int weight) {
            this.weight = weight;
        }
    }
}
