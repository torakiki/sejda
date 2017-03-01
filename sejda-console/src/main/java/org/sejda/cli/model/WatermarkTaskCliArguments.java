/*
 * Created on 22 ott 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.model;

import org.sejda.conversion.DimensionAdapter;
import org.sejda.conversion.FileSourceAdapter;
import org.sejda.conversion.PageRangeSetAdapter;
import org.sejda.conversion.PointAdatper;
import org.sejda.conversion.WatermarkLocationAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the Watermark task
 * 
 * @author Andrea Vacondio
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " watermark")
public interface WatermarkTaskCliArguments extends CliArgumentsWithPdfAndFileOrDirectoryOutput,
        CliArgumentsWithPrefixableOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "s", description = "page selection. You can set a subset of pages where the watermark will be applied. Accepted values: 'num1-num2' or 'num-' or 'num1,num2-num3..' (EX. -s 4,12-14,8,20-) (optional)")
    PageRangeSetAdapter getPageSelection();

    boolean isPageSelection();

    @Option(shortName = "l", description = "watermark location relative to the page content. { behind, over }. Default is 'behind'  (optional)", defaultValue = "behind")
    WatermarkLocationAdapter getLocation();

    @Option(shortName = "a", description = "watermark opacity, between 0 (transparent) and 100 (fully opaque). Default is 100. (optional)", defaultValue = "100")
    Integer getAlpha();

    @Option(shortName = "w", description = "watermark image existing file (jpg, tif, png, gif, bmp). (EX. -w /tmp/logo.jpg) (required)")
    FileSourceAdapter getWatermark();

    @Option(shortName = "d", description = "watermark dimension in the form WIDTHxHEIGHT. Defaults to the watermark image dimensions. (EX. -d 253x180) (optional)")
    DimensionAdapter getDimension();

    boolean isDimension();

    @Option(shortName = "c", description = "watermark position coordinates in the form x,y. (EX. -c 15,100) (required)")
    PointAdatper getPosition();

}
