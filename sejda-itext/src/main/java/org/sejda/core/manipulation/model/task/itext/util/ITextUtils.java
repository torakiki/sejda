/*
 * Created on 04/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.task.itext.util;

import org.sejda.core.manipulation.model.task.itext.component.PdfCopier;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;

import com.lowagie.text.pdf.PdfReader;

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
    public static void nullSafeClosePdfStamperHandler(PdfStamperHandler stamperHandler) {
        if (stamperHandler != null) {
            stamperHandler.closePdfStamper();
        }
    }

    /**
     * Null safe close of the {@link PdfCopier}
     * 
     * @param copyHandler
     */
    public static void nullSafeClosePdfCopy(PdfCopier copier) {
        if (copier != null) {
            copier.close();
        }
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
