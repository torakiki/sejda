/*
 * Created on 30/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.conversion.BasePageRangeAdapter.PageRangeWithAllAdapter;
import org.sejda.conversion.HorizontalAlignAdapter;
import org.sejda.conversion.StandardType1FontAdapter;
import org.sejda.conversion.VerticalAlignAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the SetHeaderFooterTask task
 * 
 * @author Andrea Vacondio
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setheaderfooter")
public interface SetHeaderFooterTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput {

    @Option(shortName = "s", description = "page range selection. You can set the pages where the header/footer will be applied. Accepted values: 'all' or 'num1-num2' or"
            + " 'num-' (EX. -s 12-14) (required)")
    PageRangeWithAllAdapter getPageRange();

    @Option(shortName = "x", description = "horizontal align { center, right, left }. Default is 'center' (optional)", defaultValue = "center")
    HorizontalAlignAdapter getHorizontalAlign();

    boolean isHorizontalAlign();

    @Option(shortName = "y", description = "vertical align defining if it's a header (top) or a footer (bottom). { top, bottom }. Default is 'bottom' (optional)", defaultValue = "bottom")
    VerticalAlignAdapter getVerticalAlign();

    boolean isVerticalAlign();

    @Option(shortName = "t", description = "font as a standard font type 1, defined in Pdf reference 1.7, section 5.5.1. "
            + "{ Times-Roman, Times-Bold, Times-Italic, Times-BoldItalic, Helvetica, Helvetica-Bold, Helvetica-Oblique, Helvetica-BoldOblique, Courier, Courier-Bold"
            + ", Courier-Oblique, Courier-BoldOblique, Symbol, ZapfDingbats }. Default is 'Helvetica' (optional)", defaultValue = "Helvetica")
    StandardType1FontAdapter getFont();

    boolean isFont();

    @Option(shortName = "d", description = "font size in pt. Default is 10 (optional)", defaultValue = "10")
    double getFontSize();

    boolean isFontSize();

    @Option(shortName = "l", description = "label for the header/footer. Supports dynamic patterns such as [PAGE_ROMAN], [PAGE_ARABIC], [PAGE_OF_TOTAL], [DATE], [BATES_NUMBER] and [FILE_NUMBER]. Example: \"Page [PAGE_ARABIC]\" (required)")
    String getLabel();
}
