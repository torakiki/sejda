/*
 * Created on 30/dic/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
