/*
 * Created on 06/mar/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
