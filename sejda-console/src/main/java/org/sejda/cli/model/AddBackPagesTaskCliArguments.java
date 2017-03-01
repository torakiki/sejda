/*
 * Created on 21 gen 2016
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

import java.util.List;

import org.sejda.conversion.PageRangeSetAdapter;
import org.sejda.conversion.PdfFileSourceAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Command line arguments for add back pages task
 * 
 * @author Andrea Vacondio
 *
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " addbackpages")
public interface AddBackPagesTaskCliArguments
 extends CliArgumentsWithPdfAndFileOrDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "n", description = "back pages are added every 'n' pages (default is 1) (optional)", defaultValue = "1")
    int getStep();

    @Option(shortName = "s", description = "back pages selection. You can set a subset of pages of the back pages source document to be added to the input documents. Order of the pages is relevant. Accepted values: 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (default is all the pages) (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();

    @Option(shortName = "b", description = "back pages source. A single pdf file to take back pages from: (EX. -b /tmp/file1.pdf or -b /tmp/password_protected_file2.pdf:secret123) (required)")
    List<PdfFileSourceAdapter> getBackPagesSource();
}
