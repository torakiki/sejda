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
package org.sejda.cli.model;

import java.util.List;

import org.sejda.cli.model.adapter.PdfFileSourceAdapter;
import org.sejda.conversion.PredefinedSetOfPagesAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the SimpleSplit task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " simplesplit")
public interface SimpleSplitTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput,
        CliArgumentsWithPrefixableOutput {

    // pdfsam-incompatibility no default, and this is part of a larger task: split
    @Option(shortName = "s", description = "predefined pages mode. Accepted values are 'all', 'odd' or 'even' (required)")
    PredefinedSetOfPagesAdapter getPredefinedPages();

    // override default -f option that is described as expecting a list of files with a description stating that it is expecting a single file
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}