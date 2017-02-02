/*
 * Created on 13/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * CLI interface for the PdfToJpeg task
 * 
 * @author Andrea Vacondio
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " pdftojpeg")
public interface PdfToJpegTaskCliArguments extends CliArgumentsWithImageAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "q", description = "image quality, between 0 (lowest quality, highest compression) and 100 (highest quality, lowest compression). Default is 100. (optional)", defaultValue = "100")
    Integer getQuality();
}
