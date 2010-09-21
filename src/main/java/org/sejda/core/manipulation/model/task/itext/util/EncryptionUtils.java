/*
 * Created on 17/set/2010
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
package org.sejda.core.manipulation.model.task.itext.util;

import java.util.HashMap;
import java.util.Map;

import org.sejda.core.manipulation.model.pdf.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.PdfEncryption;

import com.itextpdf.text.pdf.PdfWriter;

/**
 * Utility methods related to the encryption functionalities
 * 
 * @author Andrea Vacondio
 * 
 */
public final class EncryptionUtils {

    private static Map<PdfAccessPermission, Integer> permissionsCache = new HashMap<PdfAccessPermission, Integer>();
    static {
        permissionsCache.put(PdfAccessPermission.ANNOTATION, PdfWriter.ALLOW_MODIFY_ANNOTATIONS);
        permissionsCache.put(PdfAccessPermission.ASSEMBLE, PdfWriter.ALLOW_ASSEMBLY);
        permissionsCache.put(PdfAccessPermission.COPY, PdfWriter.ALLOW_COPY);
        permissionsCache.put(PdfAccessPermission.DEGRADATED_PRINT, PdfWriter.ALLOW_DEGRADED_PRINTING);
        permissionsCache.put(PdfAccessPermission.EXTRACTION_FOR_DISABLES, PdfWriter.ALLOW_SCREENREADERS);
        permissionsCache.put(PdfAccessPermission.FILL_FORMS, PdfWriter.ALLOW_FILL_IN);
        permissionsCache.put(PdfAccessPermission.MODIFY, PdfWriter.ALLOW_MODIFY_CONTENTS);
        permissionsCache.put(PdfAccessPermission.PRINT, PdfWriter.ALLOW_PRINTING);
    }

    private EncryptionUtils() {
        // utility
    }

    /**
     * Mapping between Sejda and iText encryption algorithms
     * 
     * @param encryption
     * @return the iText encryption constant
     */
    public static int getEncryptionAlgorithm(PdfEncryption encryption) {
        switch (encryption) {
        case AES_ENC_128:
            return PdfWriter.ENCRYPTION_AES_128;
        case STANDARD_ENC_128:
            return PdfWriter.STANDARD_ENCRYPTION_128;
        default:
            return PdfWriter.STANDARD_ENCRYPTION_40;
        }
    }

    /**
     * Mapping between Sejda and iText access permission constants
     * 
     * @param permission
     * @return the iText access permission constant
     */
    public static Integer getAccessPermission(PdfAccessPermission permission) {
        return permissionsCache.get(permission);
    }
}
