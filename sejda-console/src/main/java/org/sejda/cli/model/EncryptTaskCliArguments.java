/*
 * Created on Jul 9, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.model;

import java.util.List;

import org.sejda.conversion.PdfAccessPermissionAdapter;
import org.sejda.conversion.PdfEncryptionAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the Encrypt task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " encrypt")
public interface EncryptTaskCliArguments extends CliArgumentsWithPdfAndFileOrDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "l", description = "permissions: a list of permissions. { print, modify, copy, modifyannotations, fill, screenreaders, assembly, degradedprinting }  (optional)")
    List<PdfAccessPermissionAdapter> getAllow();

    boolean isAllow();

    @Option(shortName = "u", description = "user password for the document (optional)", defaultValue = "")
    String getUserPassword();

    @Option(shortName = "a", description = "owner password for the document (optional)", defaultValue = "")
    String getAdministratorPassword();

    @Option(shortName = "e", description = "encryption algorithm {rc4_128, aes_128, aes_256}. If omitted it uses rc4_128 (optional)", defaultValue = "rc4_128")
    PdfEncryptionAdapter getEncryptionType();
}
