/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.Collections;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.pdf.encryption.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.encryption.PdfEncryption;

/**
 * Tests for the EncryptTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class EncryptTaskTest extends AbstractTaskTest {

    public EncryptTaskTest() {
        super(TestableTask.ENCRYPT);
    }

    @Test
    public void testOutputPrefix_Specified() {
        EncryptParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        EncryptParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

    public void testPasswords_Specified() {
        EncryptParameters parameters = defaultCommandLine().with("-u", "user_pass").with("-a", "admin_pass")
                .invokeSejdaConsole();
        assertEquals("user_pass", parameters.getUserPassword());
        assertEquals("admin_pass", parameters.getOwnerPassword());
    }

    @Test
    public void testPasswords_Defaults() {
        EncryptParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getUserPassword());
        assertEquals("", parameters.getOwnerPassword());
    }

    public void testEncryptionType_Specified() {
        EncryptParameters parameters = defaultCommandLine().with("-e", "AES_ENC_128").invokeSejdaConsole();
        assertEquals(PdfEncryption.AES_ENC_128, parameters.getEncryptionAlgorithm());
    }

    @Test
    public void testEncryptionType_Default() {
        EncryptParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(PdfEncryption.STANDARD_ENC_128, parameters.getEncryptionAlgorithm());
    }

    @Test
    public void noPermissions() {
        EncryptParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(Collections.EMPTY_SET, parameters.getPermissions());
    }

    @Test
    public void testPermissions() {
        EncryptParameters parameters = defaultCommandLine().with("--allow",
                "print modify copy modifyannotations fill screenreaders assembly degradedprinting")
                .invokeSejdaConsole();
        assertThat(parameters.getPermissions(), hasItems(PdfAccessPermission.values()));
    }
}
