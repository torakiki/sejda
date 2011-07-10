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
public class EncryptConsoleTest extends BaseCommandConsoleTest {

    @Override
    String getCommandName() {
        return "encrypt";
    }

    @Test
    public void testExecuteCommandHelp() {
        assertConsoleOutputContains(
                "-h " + getCommandName(),
                "Usage: sejda-console encrypt options",
                "[--compressed] : compress output file (optional)",
                "--files -f value... : pdf files to decrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf) (required)",
                "--output -o value : output directory (required)",
                "--outputPrefix -p value : prefix for the output files name (optional)",
                "[--overwrite] : overwrite existing output file (optional)",
                "--pdfVersion -v value : pdf version of the output document/s. (optional)",
                "--adminstratorPassword -a value : administrator password for the document (optional)",
                "--encryptionType -e value : encryption angorithm {STANDARD_ENC_40, STANDARD_ENC_128, AES_ENC_128}. If omitted it uses STANDARD_ENC_128 (optional)",
                "--userPassword -u value : user password for the document (optional)");
    }

    @Test
    public void testOutputPrefix_Specified() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getBaseCommandWithDefaults()
                + " -p fooPrefix");
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getBaseCommandWithDefaults());
        assertEquals("encrypted_", parameters.getOutputPrefix());
    }

    public void testPasswords_Specified() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getBaseCommandWithDefaults()
                + " -u user_pass -a admin_pass");
        assertEquals("user_pass", parameters.getUserPassword());
        assertEquals("admin_pass", parameters.getOwnerPassword());
    }

    @Test
    public void testPasswords_Defaults() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getBaseCommandWithDefaults());
        assertEquals("", parameters.getUserPassword());
        assertEquals("", parameters.getOwnerPassword());
    }

    public void testEncryptionType_Specified() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getBaseCommandWithDefaults()
                + " -e AES_ENC_128");
        assertEquals(PdfEncryption.AES_ENC_128, parameters.getEncryptionAlgorithm());
    }

    @Test
    public void testEncryptionType_Default() {
        EncryptParameters parameters = invokeConsoleAndReturnTaskParameters(getBaseCommandWithDefaults());
        assertEquals(PdfEncryption.STANDARD_ENC_128, parameters.getEncryptionAlgorithm());
    }
}
