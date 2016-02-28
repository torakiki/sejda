/*
 * Created on Sep 12, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.model;

import java.util.List;

import org.sejda.conversion.PdfPageLabelAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the SetPageLabels task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setpagelabels")
public interface SetPageLabelsTaskCliArguments extends CliArgumentsWithPdfFileOutput, SinglePdfSourceTaskCliArguments {

    // pdfsam-incompatibility with extra labelPrefix option in the string format
    @Option(shortName = "l", description = "labels definition. Accepted string is \"pageFrom:numberingStyle:labelSuffixStartNumber:labelPrefix\" where "
            + "pageFrom is the index of the starting page within the document, numberingStyle is the suffix label number style "
            + "('arabic', 'uroman', 'lroman', 'uletter', 'lletter', 'empty'), "
            + "labelSuffixStartNumber is usually 1 and represents the number from where the labels start to increment from, labelPrefix is optional and defines "
            + "the text to be appended before the label numbering (required)")
    List<PdfPageLabelAdapter> getLabels();
}