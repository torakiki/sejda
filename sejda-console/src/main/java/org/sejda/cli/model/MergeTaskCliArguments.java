/*
 * Created on Sep 3, 2011
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

import org.sejda.cli.model.adapter.MultiplePageRangeSetAdapter;
import org.sejda.cli.model.adapter.PdfFileSourceListAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the Concat/Merge task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " merge")
public interface MergeTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(description = "input pdf documents contain forms (high memory usage) (optional)")
    boolean isCopyFields();

    @Option(shortName = "d", description = "directory containing pdf files to concat. Files will be merged in alphabetical order. (optional)")
    PdfFileSourceListAdapter getDirectory();

    boolean isDirectory();

    // TODO: make sure optional options are reflected in the jewelcli produced help output, add isXXX
    @Option(shortName = "s", description = "page selection script. You can set a subset of pages to merge. Accepted values: 'all' or 'num1-num2' or"
            + " 'num-' or 'num1,num2-num3..' (EX. -f /tmp/file1.pdf /tmp/file2.pdf -s all:all:), "
            + "(EX. -f /tmp/file1.pdf /tmp/file2.pdf /tmp/file3.pdf -s all:12-14:32,12-14,4,34-:) to merge file1.pdf and pages 12,13,14 of file2.pdf. "
            + "If -s is not set default behaviour is to merge document completely (optional)", defaultValue = "")
    MultiplePageRangeSetAdapter getPageSelection();

    boolean isFiles();

    @Option(shortName = "l", description = "xml or csv file containing pdf files list to concat. If csv file in comma separated value format; "
            + "if xml file <filelist><filevalue=\"filepath\" /></filelist> (optional)")
    PdfFileSourceListAdapter getFilesListConfig();

    boolean isFilesListConfig();

}
