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
package org.sejda.cli.model;

import java.util.List;

import org.sejda.cli.model.adapter.PdfAccessPermissionAdapter;
import org.sejda.cli.model.adapter.PdfEncryptionAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the Encrypt task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " encrypt")
public interface EncryptTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput {

    @Option(shortName = "l", description = "permissions: a list of permissions. { print, modify, copy, modifyannotations, fill, screenreaders, assembly, degradedprinting}  (optional)")
    List<PdfAccessPermissionAdapter> getAllow();

    boolean isAllow();

    @Option(shortName = "u", description = "user password for the document (optional)", defaultValue = "")
    String getUserPassword();

    @Option(shortName = "a", description = "owner password for the document (optional)", defaultValue = "")
    String getAdminstratorPassword();

    @Option(shortName = "e", description = "encryption angorithm {rc4_40, rc4_128, aes_128}. If omitted it uses rc4_128 (optional)", defaultValue = "rc4_128")
    PdfEncryptionAdapter getEncryptionType();
}
