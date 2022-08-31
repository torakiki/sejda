/*
 * Created on 22 ott 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.conversion;

import org.junit.jupiter.api.Test;
import org.sejda.conversion.exception.ConversionException;

import java.awt.Dimension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 *
 */
public class DimensionAdapterTest {
    @Test
    public void positives() {
        assertEquals(new Dimension(100, 50), new DimensionAdapter("100x50").getDimension());
    }

    @Test
    public void missing() {
        assertThrows(ConversionException.class, () -> new DimensionAdapter("10"));
    }

    @Test
    public void invalidX() {
        assertThrows(ConversionException.class, () -> new DimensionAdapter("Chuckx10"));
    }

    @Test
    public void invalidY() {
        assertThrows(ConversionException.class, () -> new DimensionAdapter("10xNorris"));
    }
}
