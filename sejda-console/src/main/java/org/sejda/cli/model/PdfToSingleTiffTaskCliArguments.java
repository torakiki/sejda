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
package org.sejda.cli.model;

import java.util.List;

import org.sejda.conversion.ImageColorTypeAdapter;
import org.sejda.conversion.PdfFileSourceAdapter;
import org.sejda.conversion.TiffCompressionTypeAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * CLI interface for the PdfToSingleTiff task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " pdftosingletiff")
public interface PdfToSingleTiffTaskCliArguments extends CliArgumentsWithImageFileOutput {

    @Option(shortName = "x", description = "image compression type: {none, ccitt_group_3_1d, ccitt_group_3_2d, ccitt_group_4, lzw, jpeg_ttn2, packbits, deflate}. Default is 'none' (optional)", defaultValue = "NONE")
    TiffCompressionTypeAdapter getCompressionType();

    // override default -f option that is described as expecting a list of files with a description stating that it is expecting a single file
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();

    @Option(shortName = "c", description = "image color type: { black_and_white, gray_scale, color_rgb } (required)")
    ImageColorTypeAdapter getColorType();

}
