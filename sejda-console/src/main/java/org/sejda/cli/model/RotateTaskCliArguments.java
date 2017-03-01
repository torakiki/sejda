/*
 * Created on Jul 10, 2011
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

import java.util.List;

import org.sejda.conversion.PageRangeSetAdapter;
import org.sejda.conversion.PredefinedSetOfPagesAdapter;
import org.sejda.conversion.RotationAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the Rotate task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " rotate")
public interface RotateTaskCliArguments extends CliArgumentsWithPdfAndFileOrDirectoryOutput, CliArgumentsWithPrefixableOutput,
        MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "r", description = "rotation degrees: 90, 180 or 270. Pages will be rotated clockwise (optional)")
    RotationAdapter getRotation();

    boolean isRotation();

    @Option(shortName = "k", description = "per page rotation degrees: 90, 180 or 270. Ex: -s 4,5,6,7,8,9 -k 90 180 90 180 270 90 (optional)")
    List<RotationAdapter> getPageRotations();

    boolean isPageRotations();

    @Option(shortName = "m", description = "predefined pages: all, odd or even (optional)")
    PredefinedSetOfPagesAdapter getPredefinedPages();

    boolean isPredefinedPages();

    @Option(shortName = "s", description = "page selection. You can set a subset of pages to rotate. Order of the pages is relevant. Accepted values: 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();
}
