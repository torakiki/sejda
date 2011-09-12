/*
 * Created on Sep 12, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli;

import org.sejda.cli.adapters.PageRangeSetAdapter;
import org.sejda.core.manipulation.model.pdf.page.PredefinedSetOfPages;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the ExtractPages task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " extractpages")
public interface ExtractPagesTaskCliArguments extends CliArgumentsWithFileOutput {

    @Option(shortName = "p", description = "predefined pages mode. Accepted values are ALL_PAGES, ODD_PAGES or EVEN_PAGES (optional)")
    PredefinedSetOfPages getPredefinedPages();

    boolean isPredefinedPages();

    @Option(shortName = "s", description = "page selection script. You can set a subset of pages to merge. Accepted values: 'all' or 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s all or -s 4,12-14,8,20-)s (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();
}