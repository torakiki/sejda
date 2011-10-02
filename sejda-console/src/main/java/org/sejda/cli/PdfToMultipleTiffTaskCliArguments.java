/*
 * Created on Oct 2, 2011
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

import org.sejda.core.manipulation.model.image.TiffCompressionType;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * CLI interface for the PdfToMultipleTiff task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " pdftomultipletiff")
public interface PdfToMultipleTiffTaskCliArguments extends CliArgumentsWithImageAndDirectoryOutput {

    @Option(description = "image compression type: NONE, CCITT_GROUP_3_1D, CCITT_GROUP_3_2D, CCITT_GROUP_4, LZW, JPEG_TTN2, PACKBITS, DEFLATE. Default is NONE", defaultValue = "NONE")
    TiffCompressionType getCompressionType();
}
