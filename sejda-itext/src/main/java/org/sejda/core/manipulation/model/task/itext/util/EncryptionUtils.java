/*
 * Created on 17/set/2010
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.manipulation.model.pdf.encryption.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.encryption.PdfEncryption;

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
        permissionsCache.put(PdfAccessPermission.COPY, PdfWriter.ALLOW_COPY);
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
