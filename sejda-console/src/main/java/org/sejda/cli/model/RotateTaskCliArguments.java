/*
 * Created on Jul 10, 2011
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
package org.sejda.cli.model;

import org.sejda.conversion.PageRangeSetAdapter;
import org.sejda.conversion.RotationAdapter;

import org.sejda.conversion.PredefinedSetOfPagesAdapter;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the Rotate task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " rotate")
public interface RotateTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput, CliArgumentsWithPrefixableOutput {

    @Option(shortName = "r", description = "rotation degrees: 90, 180 or 270. Pages will be rotated clockwise (required)")
    RotationAdapter getRotation();

    @Option(shortName = "m", description = "predefined pages: all, odd or even (optional)")
    PredefinedSetOfPagesAdapter getPredefinedPages();

    boolean isPredefinedPages();

    @Option(shortName = "s", description = "page selection. You can set a subset of pages to rotate. Order of the pages is relevant. Accepted values: 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();
}
