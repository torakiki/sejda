/*
 * Created on 01 dic 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.model;

import org.sejda.conversion.ScaleTypeAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the scale task
 * 
 * @author Andrea Vacondio
 *
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " scale")
public interface ScaleTaskCliArguments extends CliArgumentsWithPdfAndFileOrDirectoryOutput, CliArgumentsWithPrefixableOutput,
        MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "t", description = "type of scale to perform. { content, page }. Default is 'content' (optional)", defaultValue = "content")
    ScaleTypeAdapter getType();

    @Option(shortName = "s", description = "scale to apply. A floating point number (Ex. --scale 0.8 will scale to 80% while --scale 1.2 will scale to 120%) (required)")
    Double getScale();
}
