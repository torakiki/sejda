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

import org.sejda.conversion.*;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the SetHeaderFooterTask task
 * 
 * @author Andrea Vacondio
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setheaderfooter")
public interface SetHeaderFooterTaskCliArguments extends CliArgumentsWithPrefixableOutput,
        CliArgumentsWithPdfAndFileOrDirectoryOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "s", description = "page range selection. You can set the pages where the header/footer will be applied. Accepted values: 'all', 'odd', 'even' or 'num1-num2' or"
            + " 'num1-num2,num3,num4-num5' (EX. -s 2- or -s 2,4-8,12-14) (optional)", defaultValue = "all")
    PageRangeSetOrPredefinedSetOfPagesAdapter getPageRange();

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

    @Option(shortName = "l", description = "label for the header or footer. Supports dynamic patterns such as [PAGE_ROMAN], [PAGE_ARABIC], [PAGE_OF_TOTAL], [TOTAL_PAGES_ARABIC], [TOTAL_PAGES_ROMAN], [DATE], [BATES_NUMBER] and [FILE_NUMBER]. Example: \"Page [PAGE_ARABIC] of [TOTAL_PAGES_ARABIC]\" (required)")
    String getLabel();

    @Option(shortName = "b", description = "bates sequence start from. (optional)")
    Long getBatesStartFrom();

    boolean isBatesStartFrom();

    @Option(shortName = "i", description = "bates sequence increment. (optional)")
    Integer getBatesIncrement();

    boolean isBatesIncrement();

    @Option(shortName = "k", description = "page number counter start from. Defaults to 1, but can be overridden to start from another offset. Ex: -k 5 -s 5-10 (optional)")
    Integer getPageCountStartFrom();

    boolean isPageCountStartFrom();

    @Option(shortName = "c", description = "font color. Defaults to black #000000. Ex: -c #AA3399 (optional)", defaultValue = "#000000")
    String getFontColor();

    boolean isFontColor();

    @Option(shortName = "g", description = "file numbering start from. Defaults to 1, but can be overridden to start from another offset. Ex: 10 (optional)", defaultValue = "1")
    Integer getFileCountStartFrom();

    boolean isFileCountStartFrom();
}
