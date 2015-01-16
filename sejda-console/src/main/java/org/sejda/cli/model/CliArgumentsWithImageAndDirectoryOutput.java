/*
 * Created on Aug 22, 2011
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

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * 
 * Base interface for specifying of the command line interface for tasks that have output configured as a <i>directory</i> and format as <i>image</i>
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CliArgumentsWithImageAndDirectoryOutput extends CliArgumentsWithImageOutput,
        CliArgumentsWithDirectoryOutput {

    @Option(shortName = "s", description = "page selection script. You can set a subset of pages to convert. Accepted values: 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();
}
