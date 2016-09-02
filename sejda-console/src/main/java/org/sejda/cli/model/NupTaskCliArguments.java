/*
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

@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " nup")
public interface NupTaskCliArguments
        extends CliArgumentsWithPdfAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "n", description = "number of pages per sheet. Only powers of 2 currently supported (2, 4, 8, 16, 32, etc). Defaults to 2. (Ex --n 4) (optional)", defaultValue = "4")
    Integer getN();

    @Option(shortName = "x", description = "vertical page ordering in sheets, top-down and then left-right. Default is horizontal ordering (left-right and then top-down) (optional)")
    boolean isVerticalOrdering();

    @Option(shortName = "s", description = "preserve original page size, scaling down the collated pages. Default is false (nup 2 of A4 will produce A3, nup 4 of A4 will produce A2 etc) (optional)")
    boolean isPreservePageSize();
}
