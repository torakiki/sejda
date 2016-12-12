/*
 * Created on 12 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;
import org.sejda.sambox.encryption.StandardSecurity;
import org.sejda.sambox.encryption.StandardSecurityEncryption;

/**
 * @author Andrea Vacondio
 *
 */
public class EncryptionUtilsTest {

    @Test
    public void testSecurityFromParams() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_256);
        params.setOwnerPassword("Chuck");
        params.setUserPassword("Norris");
        params.addPermission(PdfAccessPermission.MODIFY);
        params.addPermission(PdfAccessPermission.ANNOTATION);
        StandardSecurity security = EncryptionUtils.securityFromParams(params);
        assertEquals("Chuck", security.ownerPassword);
        assertEquals("Norris", security.userPassword);
        assertEquals(StandardSecurityEncryption.AES_256, security.encryption);
        assertTrue(security.permissions.canModify());
        assertTrue(security.permissions.canModifyAnnotations());
        assertTrue(security.permissions.canExtractForAccessibility());
        assertFalse(security.permissions.canPrint());
        assertFalse(security.permissions.canPrintDegraded());
        assertFalse(security.permissions.canAssembleDocument());
        assertFalse(security.permissions.canExtractContent());
        assertFalse(security.permissions.canFillInForm());
    }

}
