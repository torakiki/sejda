/*
 * Created on 17 gen 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.pdf.PdfVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfVersionAdapterWithFallbackTest {
    @Test
    public void testPositiveFallback() {
        assertEquals(PdfVersion.VERSION_1_4, new PdfVersionAdapterWithFallback("4").getVersion());
    }

    @Test
    public void testPositive() {
        assertEquals(PdfVersion.VERSION_1_6, new PdfVersionAdapterWithFallback("1.6").getVersion());
    }

    @Test
    public void missingPoint() {
        assertThrows(SejdaRuntimeException.class, () -> new PdfVersionAdapterWithFallback("9").getVersion());
    }

}
