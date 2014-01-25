/*
 * Created on Jun 30, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.conversion.PdfFileSourceAdapter;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Base interface for specifications of the command line interface for {@link org.sejda.model.task.Task}s
 * 
 * @author Eduard Weissmann
 * 
 */
public interface TaskCliArguments {
    String EXECUTABLE_NAME = "sejda-console";
    String FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST = "pdf files to operate on: a list of existing pdf files (EX. -f /tmp/file1.pdf or -f /tmp/password_protected_file2.pdf:secret123) (required)";
    String FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST_OPTIONAL = "pdf files to operate on: a list of existing pdf files (EX. -f /tmp/file1.pdf or -f /tmp/password_protected_file2.pdf:secret123) (optional)";
    String FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE = "pdf file to operate on: a single pdf file (EX. -f /tmp/file1.pdf or -f /tmp/password_protected_file2.pdf:secret123) (required)";

    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST)
    List<PdfFileSourceAdapter> getFiles();

    @Option(description = "overwrite existing output file (optional)")
    boolean getOverwrite();

    @Option(shortName = "h", description = "prints usage information. Can be used to detail options for a command '-h command' (optional)")
    boolean isHelp();
}
