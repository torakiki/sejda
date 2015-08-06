/*
 * Created on 17/set/2010
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;

import com.lowagie.text.pdf.PdfWriter;

/**
 * Utility methods related to the encryption functionalities.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class EncryptionUtils {

    private static final Map<PdfAccessPermission, Integer> PERMISSIONS_CACHE;
    static {
        Map<PdfAccessPermission, Integer> permissionsCache = new HashMap<PdfAccessPermission, Integer>();
        permissionsCache.put(PdfAccessPermission.ANNOTATION, PdfWriter.ALLOW_MODIFY_ANNOTATIONS);
        permissionsCache.put(PdfAccessPermission.ASSEMBLE, PdfWriter.ALLOW_ASSEMBLY);
        permissionsCache.put(PdfAccessPermission.COPY_AND_EXTRACT, PdfWriter.ALLOW_COPY);
        permissionsCache.put(PdfAccessPermission.DEGRADATED_PRINT, PdfWriter.ALLOW_DEGRADED_PRINTING);
        permissionsCache.put(PdfAccessPermission.EXTRACTION_FOR_DISABLES, PdfWriter.ALLOW_SCREENREADERS);
        permissionsCache.put(PdfAccessPermission.FILL_FORMS, PdfWriter.ALLOW_FILL_IN);
        permissionsCache.put(PdfAccessPermission.MODIFY, PdfWriter.ALLOW_MODIFY_CONTENTS);
        permissionsCache.put(PdfAccessPermission.PRINT, PdfWriter.ALLOW_PRINTING);
        PERMISSIONS_CACHE = Collections.unmodifiableMap(permissionsCache);
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
        return PERMISSIONS_CACHE.get(permission);
    }
}
