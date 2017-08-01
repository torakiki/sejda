/*
 * Created on 1/aug/2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.scale;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.TestUtils;

public class MarginsTest {
    @Test
    public void testEquals() {
        Margins eq1 = new Margins(0, 1, 10, 9);
        Margins eq2 = new Margins(0, 1, 10, 9);
        Margins eq3 = new Margins(0, 1, 10, 9);
        Margins diff = new Margins(1, 1, 10, 9);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void inchesToPoints() {
        assertEquals(0, Margins.inchesToPoints(0), 0);
        assertEquals(144, Margins.inchesToPoints(2), 0);
        assertEquals(100.8, Margins.inchesToPoints(1.4), 0);
    }
}
