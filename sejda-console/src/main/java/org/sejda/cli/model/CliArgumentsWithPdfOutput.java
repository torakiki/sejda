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

import org.sejda.conversion.PdfVersionAdapter;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Trait for cli tasks that output pdf files
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CliArgumentsWithPdfOutput extends TaskCliArguments {

    @Option(description = "compress output file (optional)")
    boolean getCompressed();

    @Option(shortName = "v", description = "pdf version of the output document/s {2, 3, 4, 5, 6 or 7}. Default is 6. (optional)", defaultValue = "6")
    PdfVersionAdapter getPdfVersion();
}
