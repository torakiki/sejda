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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

/**
 * @author Andrea Vacondio
 * 
 */
public class PageNumberWithPdfPageTransitionAdapterTest {

    @Test(expected = ConversionException.class)
    public void testNegative() {
        new PageNumberWithPdfPageTransitionAdapter("box_inward:2:3");
    }

    @Test
    public void testPositive() {
        PageNumberWithPdfPageTransitionAdapter victim = new PageNumberWithPdfPageTransitionAdapter("box_inward:2:3:5");
        assertEquals(5, victim.getPageNumber().intValue());
        assertNotNull(victim.getPdfPageTransition());
        assertEquals(PdfPageTransitionStyle.BOX_INWARD, victim.getPdfPageTransition().getStyle());
        assertEquals(2, victim.getPdfPageTransition().getTransitionDuration());
        assertEquals(3, victim.getPdfPageTransition().getDisplayDuration());
    }
}
