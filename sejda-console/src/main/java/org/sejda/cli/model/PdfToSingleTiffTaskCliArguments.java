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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
