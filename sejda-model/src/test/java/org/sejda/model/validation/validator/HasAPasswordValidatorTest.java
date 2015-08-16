/*
 * Created on 06/mar/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.encryption.PdfEncryption;

/**
 * @author Andrea Vacondio
 * 
 */
public class HasAPasswordValidatorTest {
    private HasAPasswordValidator victim = new HasAPasswordValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testNoPwd() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        assertFalse(victim.isValid(params, null));
    }

    @Test
    public void testOwner() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        params.setOwnerPassword("Chuck");
        params.setUserPassword(null);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testUser() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        params.setOwnerPassword(null);
        params.setUserPassword("Chuck");
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testBoth() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        params.setOwnerPassword("Chuck");
        params.setUserPassword("Chuck");
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testEmptyOwner() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        params.setOwnerPassword("");
        params.setUserPassword("Chuck");
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testEmptyUser() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        params.setUserPassword("");
        params.setOwnerPassword("Chuck");
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testEmptyBoth() {
        EncryptParameters params = new EncryptParameters(PdfEncryption.AES_ENC_128);
        params.setOwnerPassword("");
        params.setUserPassword("");
        assertFalse(victim.isValid(params, null));
    }

}
