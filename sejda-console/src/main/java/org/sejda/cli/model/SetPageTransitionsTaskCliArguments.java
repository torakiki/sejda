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
package org.sejda.cli.model;

import java.util.List;

import org.sejda.conversion.PageNumberWithPdfPageTransitionAdapter;
import org.sejda.conversion.PdfFileSourceAdapter;
import org.sejda.conversion.PdfPageTransitionAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the SetPageTransitions task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setpagetransitions")
public interface SetPageTransitionsTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(description = "open the document in fullscreen mode (optional)")
    boolean isFullscreen();

    @Option(shortName = "d", description = "slideshow default transition effect definition. Expected format is 'transitionType:transitionDurationInSec:pageDisplayDurationInSec'. Transition types are "
            + "{'blinds_horizontal', 'blinds_vertical', 'box_inward', 'box_outward', 'cover_left_to_right', 'cover_top_to_bottom', 'dissolve', 'fade', 'fly_left_to_right', 'fly_top_to_bottom', 'glitter_diagonal', 'glitter_left_to_right', 'glitter_top_to_bottom', 'push_left_to_right', 'push_top_to_bottom', 'replace', 'split_horizontal_inward', 'split_horizontal_outward', 'split_vertical_inward', 'split_vertical_outward', 'uncover_left_to_right', 'uncover_top_to_bottom', 'wipe_bottom_to_top', 'wipe_left_to_right', 'wipe_right_to_left', 'wipe_top_to_bottom'} (optional)")
    PdfPageTransitionAdapter getDefaultTransition();

    boolean isDefaultTransition();

    @Option(shortName = "t", description = "transitions for specific pages. Expected format is 'transitionType:transitionDurationInSec:pageDisplayDurationInSec:pageNumber' (optional)")
    List<PageNumberWithPdfPageTransitionAdapter> getTransitions();

    boolean isTransitions();

    // override default -f option that is described as expecting a list of files with a description stating that it is expecting a single file
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}
