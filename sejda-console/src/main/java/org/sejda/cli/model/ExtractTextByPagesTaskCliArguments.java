/*
 * Created on Oct 25, 2013
 * Copyright 2013 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.adapter.PdfFileSourceAdapter;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

import java.util.List;

/**
 * Specifications for command line options of the ExtractTextByPages task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " extracttextbypages")
public interface ExtractTextByPagesTaskCliArguments extends CliArgumentsWithTextAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput {

    @Option(shortName = "e", description = "text encoding, default is UTF-8 (optional)", defaultValue = "UTF-8")
    String getTextEncoding();

    @Option(shortName = "n", description = "page number(s) to split at (required)")
    List<Integer> getPageNumbers();

    // override default -f option that is decribed as expecting a list of files with a description stating that it is expecting a single file
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}