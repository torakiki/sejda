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

import org.sejda.conversion.PageRangeSetAdapter;
import org.sejda.conversion.RepaginationAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;
import org.sejda.conversion.SplitDownTheMiddleModeAdapter;

@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " splitdownthemiddle")
public interface SplitDownTheMiddleTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "r", description = "repagination (Ex: --repagination last-first denotes pages are sourced from a booklet scan like (10,1) (2,9) (8,3) (4,7) (6,5) and should be reordered to natural order 1, 2, 3,etc) (optional)")
    RepaginationAdapter getRepagination();

    boolean isRepagination();

    @Option(shortName = "m", description = "Allows overriding the horizontal or vertical split mode. Accepted values are 'auto', 'horizontal' or 'vertical'. Defaults to 'auto'. (optional)", defaultValue = "auto")
    SplitDownTheMiddleModeAdapter getMode();

    @Option(shortName = "n", description = "Allows overriding the location of the split line. Ratio is top part / bottom part for horizontal split or left / right part for vertical. Defaults to 1, meaning split in equal parts. (optional)", defaultValue = "1")
    Double getRatio();

    @Option(shortName = "s", description = "pages to exclude from being split. Will be added to the result document as they are. Accepted values: 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (optional)")
    PageRangeSetAdapter getExcludedPages();

    boolean isExcludedPages();
}
