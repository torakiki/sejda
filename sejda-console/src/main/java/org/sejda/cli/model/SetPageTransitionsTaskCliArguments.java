/*
 * Created on Sep 20, 2011
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

import org.sejda.conversion.PageNumberWithPdfPageTransitionAdapter;
import org.sejda.conversion.PdfPageTransitionAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the SetPageTransitions task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setpagetransitions")
public interface SetPageTransitionsTaskCliArguments
        extends CliArgumentsWithPdfFileOutput, SinglePdfSourceTaskCliArguments {

    @Option(description = "open the document in fullscreen mode (optional)")
    boolean isFullscreen();

    @Option(shortName = "d", description = "slideshow default transition effect definition. Expected format is 'transitionType:transitionDurationInSec:pageDisplayDurationInSec'. Transition types are "
            + "{'blinds_horizontal', 'blinds_vertical', 'box_inward', 'box_outward', 'cover_left_to_right', 'cover_top_to_bottom', 'dissolve', 'fade', 'fly_left_to_right', 'fly_top_to_bottom', 'glitter_diagonal', 'glitter_left_to_right', 'glitter_top_to_bottom', 'push_left_to_right', 'push_top_to_bottom', 'replace', 'split_horizontal_inward', 'split_horizontal_outward', 'split_vertical_inward', 'split_vertical_outward', 'uncover_left_to_right', 'uncover_top_to_bottom', 'wipe_bottom_to_top', 'wipe_left_to_right', 'wipe_right_to_left', 'wipe_top_to_bottom'} (optional)")
    PdfPageTransitionAdapter getDefaultTransition();

    boolean isDefaultTransition();

    @Option(shortName = "t", description = "transitions for specific pages. Expected format is 'transitionType:transitionDurationInSec:pageDisplayDurationInSec:pageNumber' (optional)")
    List<PageNumberWithPdfPageTransitionAdapter> getTransitions();

    boolean isTransitions();
}
