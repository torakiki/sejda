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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.sejda.impl.itext.util.EncryptionUtils.getAccessPermission;
import static org.sejda.impl.itext.util.EncryptionUtils.getEncryptionAlgorithm;

import org.junit.Test;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;

import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Andrea Vacondio
 * 
 */
public class EncryptionUtilsTest {

    @Test
    public void testGetEncryptionAlgorithm() {
        assertEquals(PdfWriter.ENCRYPTION_AES_128, getEncryptionAlgorithm(PdfEncryption.AES_ENC_128));
        assertEquals(PdfWriter.STANDARD_ENCRYPTION_128, getEncryptionAlgorithm(PdfEncryption.STANDARD_ENC_128));
        assertEquals(PdfWriter.STANDARD_ENCRYPTION_40, getEncryptionAlgorithm(PdfEncryption.STANDARD_ENC_40));
    }

    @Test
    public void testGetAccessPermission() {
        assertEquals(PdfWriter.ALLOW_ASSEMBLY, getAccessPermission(PdfAccessPermission.ASSEMBLE).intValue());
        assertEquals(PdfWriter.ALLOW_COPY, getAccessPermission(PdfAccessPermission.COPY_AND_EXTRACT).intValue());
        assertEquals(PdfWriter.ALLOW_DEGRADED_PRINTING, getAccessPermission(PdfAccessPermission.DEGRADATED_PRINT)
                .intValue());
        assertEquals(PdfWriter.ALLOW_FILL_IN, getAccessPermission(PdfAccessPermission.FILL_FORMS).intValue());
        assertEquals(PdfWriter.ALLOW_MODIFY_ANNOTATIONS, getAccessPermission(PdfAccessPermission.ANNOTATION).intValue());
        assertEquals(PdfWriter.ALLOW_MODIFY_CONTENTS, getAccessPermission(PdfAccessPermission.MODIFY).intValue());
        assertEquals(PdfWriter.ALLOW_PRINTING, getAccessPermission(PdfAccessPermission.PRINT).intValue());
        assertEquals(PdfWriter.ALLOW_SCREENREADERS, getAccessPermission(PdfAccessPermission.EXTRACTION_FOR_DISABLES)
                .intValue());
    }

    @Test
    public void testAllPermissionsAreMapped() {
        for (PdfAccessPermission permission : PdfAccessPermission.values()) {
            assertNotNull(getAccessPermission(permission));
        }
    }
}
