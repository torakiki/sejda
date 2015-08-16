/*
 * Created on 04/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext.util;

import com.lowagie.text.pdf.PdfReader;

/**
 * Provides utility methods to handle iText related components.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ITextUtils {

    private ITextUtils() {
        // on purpose
    }

    /**
     * Null safe close of the input {@link PdfReader}
     * 
     * @param pdfReader
     */
    public static void nullSafeClosePdfReader(PdfReader pdfReader) {
        if (pdfReader != null) {
            pdfReader.close();
        }
    }
}
