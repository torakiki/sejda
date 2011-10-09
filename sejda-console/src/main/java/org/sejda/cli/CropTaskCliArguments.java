/*
 * Created on Sep 30, 2011
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

import org.sejda.cli.adapters.RectangularBoxAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * CLI interface for the Crop task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " crop")
public interface CropTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(description = "list of rectangles crop areas. A crop area is defined by two points: bottomLeft and topRight, int the format (bottom:left)(top:right). Ex: --cropAreas (0:0)(5:10) (5:0)(10:10)")
    List<RectangularBoxAdapter> getCropAreas();
}
