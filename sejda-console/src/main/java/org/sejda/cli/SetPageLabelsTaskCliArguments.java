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

import java.util.List;

import org.sejda.cli.adapters.PdfPageLabelAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the SetPageLabels task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " setpagelabels")
public interface SetPageLabelsTaskCliArguments extends CliArgumentsWithFileOutput {

    // TODO: EW: document pdfsam-incompatibility with extra labelPrefix option in the string format
    @Option(shortName = "l", description = "labels definition. Accepted string is \"pageFrom:numberingStyle:labelSuffixStartNumber:labelPrefix\" where "
            + "pagefrom is the index of the starting page within the document, numberingStyle is the suffix label number style "
            + "(ARABIC, UPPERCASE_ROMANS, LOWERCASE_ROMANS, EMPTY), "
            + "labelSuffixStartNumber is usually 1 and represents the number from where the labels start to increment from, labelPrefix is optional and defines "
            + "the text to be appended before the label numbering (required)")
    List<PdfPageLabelAdapter> getLabels();
}