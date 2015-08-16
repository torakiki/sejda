/*
 * Created on 16/nov/2012
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
package org.sejda.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class HorizontalAlignTest {

    @Test
    public void testPosition() {
        assertEquals(45, HorizontalAlign.CENTER.position(100, 10, 5), 0);
        assertEquals(5, HorizontalAlign.LEFT.position(100, 10, 5), 0);
        assertEquals(85, HorizontalAlign.RIGHT.position(100, 10, 5), 0);
    }
}
