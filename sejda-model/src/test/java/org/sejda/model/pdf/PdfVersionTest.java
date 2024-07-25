/*
 * Created on 28/nov/2010
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.pdf;

import org.junit.jupiter.api.Test;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfVersionTest {

    @Test
    public void testGetMaxPdfVersion() {
        assertEquals(PdfVersion.VERSION_2_0, PdfVersion.getMax(PdfVersion.values()));
    }

    @Test
    public void testGetMaxMinRequiredVersion() {
        assertEquals(PdfVersion.VERSION_1_6, PdfVersion.getMax(PdfBooleanPreference.DISPLAY_DOC_TITLE,
                PdfPageMode.FULLSCREEN, PdfPageMode.USE_ATTACHMENTS));
    }

    @Test
    public void oneNullPdfVersion() {
        assertEquals(PdfVersion.VERSION_1_7, PdfVersion.getMax(PdfVersion.VERSION_1_7, PdfVersion.VERSION_1_2, null));
    }

    @Test
    public void oneNullMinRequired() {
        assertEquals(PdfVersion.VERSION_1_7,
                PdfVersion.getMax(PdfPageMode.USE_ATTACHMENTS, PdfDuplex.DUPLEX_FLIP_LONG_EDGE, null));
    }

    @Test
    public void nullSafePdfVersion() {
        assertEquals(PdfVersion.VERSION_1_0, PdfVersion.getMax((PdfVersion) null, (PdfVersion) null));
    }

    @Test
    public void nullSafeMinRequired() {
        assertEquals(PdfVersion.VERSION_1_0, PdfVersion.getMax((PdfPageMode) null, null));
    }
}
