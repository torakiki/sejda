/*
 * Created on Jul 9, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.core.manipulation.model.pdf.encryption.PdfEncryption;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the Encrypt task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " encrypt")
public interface EncryptTaskCliArguments extends TaskCliArguments {
    @Option(shortName = "u", description = "user password for the document (optional)", defaultValue = "")
    String getUserPassword();

    @Option(shortName = "a", description = "administrator password for the document (optional)", defaultValue = "")
    String getAdminstratorPassword();

    @Option(shortName = "e", description = "encryption angorithm {STANDARD_ENC_40, STANDARD_ENC_128, AES_ENC_128}. If omitted it uses STANDARD_ENC_128 (optional)", defaultValue = "STANDARD_ENC_128")
    PdfEncryption getEncryptionType();

    @Option(shortName = "p", description = "prefix for the output files name (optional)", defaultValue = "encrypted_")
    String getOutputPrefix();
}
