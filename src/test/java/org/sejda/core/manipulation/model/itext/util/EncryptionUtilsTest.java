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
package org.sejda.core.manipulation.model.itext.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.sejda.core.manipulation.model.task.itext.util.EncryptionUtils.getAccessPermission;
import static org.sejda.core.manipulation.model.task.itext.util.EncryptionUtils.getEncryptionAlgorithm;

import org.junit.Test;
import org.sejda.core.manipulation.model.pdf.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.PdfEncryption;

import com.itextpdf.text.pdf.PdfWriter;

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
        assertEquals(PdfWriter.ALLOW_COPY, getAccessPermission(PdfAccessPermission.COPY).intValue());
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
