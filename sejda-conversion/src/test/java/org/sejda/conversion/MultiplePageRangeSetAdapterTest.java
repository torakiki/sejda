/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.conversion;

import org.junit.jupiter.api.Test;
import org.sejda.conversion.exception.ConversionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 * 
 */
public class MultiplePageRangeSetAdapterTest {

    @Test
    public void testPositive() {
        assertEquals(3, new MultiplePageRangeSetAdapter("all:12-14:32,12-14,4,34-:").ranges().size());
    }

    @Test
    public void testNegative() {
        assertThrows(ConversionException.class, () -> new MultiplePageRangeSetAdapter("all:Chuck:norris").ranges());
    }
}
