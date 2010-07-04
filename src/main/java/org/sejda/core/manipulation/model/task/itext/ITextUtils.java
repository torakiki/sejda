/*
 * Created on 04/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.task.itext;

import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Provides utility methods to handle iText related components
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ITextUtils {

    private ITextUtils() {
        // on purpose
    }

    /**
     * Null safe close of the {@link PdfStamperHandler}
     * 
     * @param stamperHandler
     */
    public static void closePdfStamperHandlerQuietly(PdfStamperHandler stamperHandler) {
        if (stamperHandler != null) {
            stamperHandler.closePdfStamper();
        }
    }

    /**
     * Null safe close of the input {@link PdfReader}
     * 
     * @param pdfReader
     */
    public static void closePdfReader(PdfReader pdfReader) {
        if (pdfReader != null) {
            pdfReader.close();
        }
    }
}
