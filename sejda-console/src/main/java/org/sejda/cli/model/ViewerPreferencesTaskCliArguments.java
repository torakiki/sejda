/*
 * Created on Jun 30, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.adapter.PdfDirectionAdapter;
import org.sejda.cli.model.adapter.PdfDuplexAdapter;
import org.sejda.cli.model.adapter.PdfNonFullScreenPageModeAdapter;
import org.sejda.cli.model.adapter.PdfPageLayoutAdapter;
import org.sejda.cli.model.adapter.PdfPageModeAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the ViewerPreferences task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setviewerpreferences")
public interface ViewerPreferencesTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput {
    @Option(description = "center of the screen (optional)")
    boolean isCenterWindow();

    @Option(description = "display document title metadata as window title (optional)")
    boolean isDisplayDocTitle();

    @Option(description = "direction {l2r, r2l}. If omitted it uses l2r (optional)", defaultValue = "l2r")
    PdfDirectionAdapter getDirection();

    @Option(description = "resize the window to fit the page size (optional)")
    boolean isFitWindow();

    @Option(description = "hide the menu bar (optional)")
    boolean isHideMenu();

    @Option(description = "hide the toolbar (optional)")
    boolean isHideToolbar();

    @Option(description = "hide user interface elements (optional)")
    boolean isHideWindowUI();

    @Option(description = "layout for the viewer. { onecolumn, singlepage, twocolumnl, twocolumnr, twopagel, twopager}  (optional)", defaultValue = "singlepage")
    PdfPageLayoutAdapter getLayout();

    @Option(description = "open mode for the viewer {attachments, fullscreen, none, ocontent, outlines, thumbs}. If omitted it uses none (optional)", defaultValue = "none")
    PdfPageModeAdapter getMode();

    @Option(description = "non full screen mode for the viewer when exiting full screen mode {nfsnone, nfsocontent, nfsoutlines, nfsthumbs}. If omitted it uses none (optional)", defaultValue = "nfsnone")
    PdfNonFullScreenPageModeAdapter getNfsMode();

    @Option(description = "no page scaling in print dialog (optional)")
    boolean isNoPrintScaling();

    // pdfsam-incompatibility: doesnt exist in pdfsam
    @Option(description = "paper handling options to use when printing the file from the print dialog: {simplex, duplex_flip_short_edge, duplex_flip_long_edge}. If ommited it uses 'simplex' (optional)", defaultValue = "simplex")
    PdfDuplexAdapter getDuplex();
}
