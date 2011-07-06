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
package org.sejda.cli;

import java.util.List;

import org.sejda.cli.adapters.PdfDirectoryOutputAdapter;
import org.sejda.cli.adapters.PdfFileSourceAdapter;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the {@link org.sejda.core.manipulation.model.task.itext.DecryptTask}
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = "sejda-console decrypt")
public interface DecryptCommandOptions extends CommandOptions {

    @Option(description = "compress output file (optional)")
    boolean getCompressed();

    @Option(shortName = "v", description = "pdf version of the output document/s. (optional)", defaultValue = "VERSION_1_6")
    PdfVersion getPdfVersion();

    @Option(shortName = "o", description = "output directory (required)")
    PdfDirectoryOutputAdapter getOutput();

    @Option(shortName = "p", description = "prefix for the output files name (optional)", defaultValue = "prefix_")
    String getOutputPrefix();

    @Option(shortName = "f", description = "pdf files to decrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf) (required)")
    List<PdfFileSourceAdapter> getFiles();

    @Option(description = "overwrite existing output file (optional)")
    boolean getOverwrite();
}
