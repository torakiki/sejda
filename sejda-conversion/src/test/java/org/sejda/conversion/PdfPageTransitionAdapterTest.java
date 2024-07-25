/*
 * Created on 27/gen/2014
 * Copyright 2014 Sober Lemur S.r.l. and Sejda BV.
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
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfPageTransitionAdapterTest {

    @Test
    public void testNegative() {
        assertThrows(ConversionException.class, () -> new PdfPageTransitionAdapter("ChuckNorris"));
    }

    @Test
    public void testPositive() {
        PdfPageTransitionAdapter victim = new PdfPageTransitionAdapter("glitter_left_to_right:2:3");
        assertEquals(PdfPageTransitionStyle.GLITTER_LEFT_TO_RIGHT, victim.getPdfPageTransition().getStyle());
        assertEquals(2, victim.getPdfPageTransition().getTransitionDuration());
        assertEquals(3, victim.getPdfPageTransition().getDisplayDuration());
    }
}
