/*
 * Created on Sep 20, 2011
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

import org.sejda.cli.adapters.PageNumberWithPdfPageTransitionAdapter;
import org.sejda.cli.adapters.PdfPageTransitionAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the SetPageTransitions task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " setpagetransitions")
public interface SetPageTransitionsTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(description = "open the document in fullscreen mode")
    boolean isFullscreen();

    @Option(description = "slideshow default transition effect definition (optional)")
    PdfPageTransitionAdapter getDefaultTransition();

    boolean isDefaultTransition();

    @Option(description = "")
    List<PageNumberWithPdfPageTransitionAdapter> getTransitions();

    boolean isTransitions();

}
