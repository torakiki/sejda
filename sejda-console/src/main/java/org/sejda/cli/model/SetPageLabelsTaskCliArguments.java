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
import org.sejda.conversion.PdfPageLabelAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the SetPageLabels task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setpagelabels")
public interface SetPageLabelsTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    // pdfsam-incompatibility with extra labelPrefix option in the string format
    @Option(shortName = "l", description = "labels definition. Accepted string is \"pageFrom:numberingStyle:labelSuffixStartNumber:labelPrefix\" where "
            + "pageFrom is the index of the starting page within the document, numberingStyle is the suffix label number style "
            + "('arabic', 'uroman', 'lroman', 'uletter', 'lletter', 'empty'), "
            + "labelSuffixStartNumber is usually 1 and represents the number from where the labels start to increment from, labelPrefix is optional and defines "
            + "the text to be appended before the label numbering (required)")
    List<PdfPageLabelAdapter> getLabels();

    // override default -f option that is described as expecting a list of files with a description stating that it is expecting a single file
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}