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
package org.sejda.cli;

import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDuplex;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageLayout;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the ViewerPreferences task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " setviewerpreferences")
public interface ViewerPreferencesTaskCliArguments extends TaskCliArguments {
    @Option(description = "center of the screen (optional)")
    boolean isCenterWindow();

    @Option(description = "display document title metadata as window title (optional)")
    boolean isDisplayDocTitle();

    // EW: pdf-sam-incompatibility {l2r, r2l} = {LEFT_TO_RIGHT, RIGHT_TO_LEFT}
    @Option(description = "direction {LEFT_TO_RIGHT, RIGHT_TO_LEFT}. If omitted it uses LEFT_TO_RIGHT (optional)", defaultValue = "LEFT_TO_RIGHT")
    PdfDirection getDirection();

    @Option(description = "resize the window to fit the page size (optional)")
    boolean isFitWindow();

    @Option(description = "hide the menu bar (optional)")
    boolean isHideMenu();

    @Option(description = "hide the toolbar (optional)")
    boolean isHideToolbar();

    @Option(description = "hide user interface elements (optional)")
    boolean isHideWindowUI();

    // EW: pdf-sam-incompatibility { onecolumn, singlepage, twocolumnl, twocolumnr, twopagel, twopager}
    @Option(description = "layout for the viewer. { ONE_COLUMN, SINGLE_PAGE, TWO_COLUMN_LEFT, TWO_COLUMN_RIGHT, TWO_PAGE_LEFT, TWO_PAGE_RIGHT}  (optional)", defaultValue = "SINGLE_PAGE")
    PdfPageLayout getLayout();

    // EW: pdf-sam-incompatibility {attachments, fullscreen, none, ocontent, outlines, thumbs}
    @Option(description = "open mode for the viewer {USE_ATTACHMENTS, FULLSCREEN, USE_NONE, USE_OC, USE_OUTLINES, USE_THUMBS}. If omitted it uses none (optional)", defaultValue = "USE_NONE")
    PdfPageMode getMode();

    // EW: pdf-sam-incompatibility {nfsnone, nfsocontent, nfsoutlines, nfsthumbs}
    @Option(description = "non full screen mode for the viewer when exiting full screen mode {USE_NONE, USE_OC, USE_OUTLINES, USE_THUMNS}. If omitted it uses none (optional)", defaultValue = "USE_NONE")
    PdfNonFullScreenPageMode getNfsMode();

    @Option(description = "no page scaling in print dialog (optional)")
    boolean isNoPrintScaling();

    @Option(description = "paper handling options to use when printing the file from the print dialog. {SIMPLEX, DUPLEX_FLIP_SHORT_EDGE, DUPLEX_FLIP_LONG_EDGE}. If ommited it uses SIMPLEX (optional)", defaultValue = "SIMPLEX")
    PdfDuplex getDuplex();
}
