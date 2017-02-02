/*
 * Created on Aug 22, 2011
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

import org.sejda.conversion.PageRangeSetAdapter;

import com.lexicalscope.jewel.cli.Option;

/**
 * 
 * Base interface for specifying of the command line interface for tasks that have output configured as a <i>directory</i> and format as <i>image</i>
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CliArgumentsWithImageAndDirectoryOutput
        extends CliArgumentsWithImageOutput, CliArgumentsWithDirectoryOutput {

    @Option(shortName = "s", description = "page selection script. You can set a subset of pages to convert. Accepted values: 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();
}
