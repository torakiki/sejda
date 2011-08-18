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

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.pdf.encryption.PdfEncryption;

/**
 * Tests for the EncryptTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class EncryptConsoleTest extends BaseTaskConsoleTest {

    @Override
    String getTaskName() {
        return "encrypt";
    }

    @Override
    protected CommandLineTestBuilder getMandatoryCommandLineArgumentsWithDefaults() {
        return new CommandLineTestBuilder(getTaskName());
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains("-h " + getTaskName(), "Usage: sejda-console encrypt options");
    }

    @Test
    public void testOutputPrefix_Specified() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-p", "fooPrefix").toString());
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals("encrypted_", parameters.getOutputPrefix());
    }

    public void testPasswords_Specified() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .with("-u", "user_pass").with("-a", "admin_pass").toString());
        assertEquals("user_pass", parameters.getUserPassword());
        assertEquals("admin_pass", parameters.getOwnerPassword());
    }

    @Test
    public void testPasswords_Defaults() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals("", parameters.getUserPassword());
        assertEquals("", parameters.getOwnerPassword());
    }

    public void testEncryptionType_Specified() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                + " -e AES_ENC_128");
        assertEquals(PdfEncryption.AES_ENC_128, parameters.getEncryptionAlgorithm());
    }

    @Test
    public void testEncryptionType_Default() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getMandatoryCommandLineArgumentsWithDefaults()
                .toString());
        assertEquals(PdfEncryption.STANDARD_ENC_128, parameters.getEncryptionAlgorithm());
    }
}
