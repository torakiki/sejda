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

import org.sejda.conversion.RepaginationAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " splitdownthemiddle")
public interface SplitDownTheMiddleTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "r", description = "repagination (Ex: --repagination last-first denotes pages are sourced from a booklet scan like (10,1) (2,9) (8,3) (4,7) (6,5) and should be reordered to natural order 1, 2, 3,etc) (optional)")
    RepaginationAdapter getRepagination();

    boolean isRepagination();
}
