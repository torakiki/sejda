/*
 * Created on Oct 2, 2011
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

import org.sejda.conversion.TiffCompressionTypeAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * CLI interface for the PdfToMultipleTiff task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " pdftomultipletiff")
public interface PdfToMultipleTiffTaskCliArguments extends CliArgumentsWithImageAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "x", description = "image compression type: {none, ccitt_group_3_1d, ccitt_group_3_2d, ccitt_group_4, lzw, jpeg_ttn2, packbits, deflate, zlib}. Default is 'none' (optional)", defaultValue = "none")
    TiffCompressionTypeAdapter getCompressionType();
}
