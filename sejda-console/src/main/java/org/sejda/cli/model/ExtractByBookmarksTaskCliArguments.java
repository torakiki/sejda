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

/**
 * Specifications for command line options of the ExtractByOutline task
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " extractbybookmarks")
public interface ExtractByBookmarksTaskCliArguments
        extends CliArgumentsWithPdfAndFileOrDirectoryOutput, CliArgumentsWithPrefixableOutput,
        CliArgumentsWithOptimizableOutput, MultiplePdfSourceTaskCliArguments, CliArgumentWithDiscardableOutline {

    @Option(shortName = "l", description = "bookmarks depth to extract by (required)")
    Integer getBookmarkLevel();

    @Option(shortName = "e", description = "matching regular expression (optional)")
    String getMatchingRegEx();

    boolean isMatchingRegEx();

}