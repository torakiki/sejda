/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;

/**
 * Tests for the EncryptTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class EncryptTaskTest extends AbstractTaskTest {

    public EncryptTaskTest() {
        super(StandardTestableTask.ENCRYPT);
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
        assertNotEquals("Random password is used as owner password when none provided by the user", "", parameters.getOwnerPassword());
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
