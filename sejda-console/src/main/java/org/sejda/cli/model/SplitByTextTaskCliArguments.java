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

import org.sejda.conversion.PdfFileSourceAdapter;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

import java.util.List;

@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " splitbytext")
public interface SplitByTextTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput {

    @Option(shortName = "t", description = "top left rectangular area's x axis coordinate (required)")
    Integer getTop();

    @Option(shortName = "l", description = "top left rectangular area's y axis coordinate (required)")
    Integer getLeft();

    @Option(shortName = "w", description = "top left rectangular area's width (required)")
    Integer getWidth();

    @Option(shortName = "g", description = "top left rectangular area's height (required)")
    Integer getHeight();

    @Option(shortName = "s", description = "prefix text should start with (Ex: --startsWith \"Fax:\") (optional)")
    String getStartsWith();

    boolean isStartsWith();

    @Override
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}
