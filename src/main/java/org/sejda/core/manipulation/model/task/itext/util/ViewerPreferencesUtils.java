/*
 * Created on 21/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.task.itext.util;

import java.util.HashMap;
import java.util.Map;

import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDuplex;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageLayout;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPrintScaling;

import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Utility methods related to the viewer preferences functionalities
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ViewerPreferencesUtils {

    private static Map<PdfDuplex, PdfName> duplexCache = new HashMap<PdfDuplex, PdfName>();
    static {
        duplexCache.put(PdfDuplex.SIMPLEX, PdfName.SIMPLEX);
        duplexCache.put(PdfDuplex.DUPLEX_FLIP_LONG_EDGE, PdfName.DUPLEXFLIPLONGEDGE);
        duplexCache.put(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE, PdfName.DUPLEXFLIPSHORTEDGE);
    }

    private static Map<PdfNonFullScreenPageMode, PdfName> nfsModeCache = new HashMap<PdfNonFullScreenPageMode, PdfName>();
    static {
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_NONE, PdfName.USENONE);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_OC, PdfName.USEOC);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_OUTLINES, PdfName.USEOUTLINES);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_THUMNS, PdfName.USETHUMBS);
    }

    private static Map<PdfPageLayout, Integer> layoutCache = new HashMap<PdfPageLayout, Integer>();
    static {
        layoutCache.put(PdfPageLayout.SINGLE_PAGE, PdfWriter.PageLayoutSinglePage);
        layoutCache.put(PdfPageLayout.ONE_COLUMN, PdfWriter.PageLayoutOneColumn);
        layoutCache.put(PdfPageLayout.TWO_COLUMN_LEFT, PdfWriter.PageLayoutTwoColumnLeft);
        layoutCache.put(PdfPageLayout.TWO_COLUMN_RIGHT, PdfWriter.PageLayoutTwoColumnRight);
        layoutCache.put(PdfPageLayout.TWO_PAGE_LEFT, PdfWriter.PageLayoutTwoPageLeft);
        layoutCache.put(PdfPageLayout.TWO_PAGE_RIGHT, PdfWriter.PageLayoutTwoPageRight);
    }

    private static Map<PdfPageMode, Integer> pageModeCache = new HashMap<PdfPageMode, Integer>();
    static {
        pageModeCache.put(PdfPageMode.USE_NONE, PdfWriter.PageModeUseNone);
        pageModeCache.put(PdfPageMode.USE_THUMBS, PdfWriter.PageModeUseThumbs);
        pageModeCache.put(PdfPageMode.USE_OUTLINES, PdfWriter.PageModeUseOutlines);
        pageModeCache.put(PdfPageMode.FULLSCREEN, PdfWriter.PageModeFullScreen);
        pageModeCache.put(PdfPageMode.USE_OC, PdfWriter.PageModeUseOC);
        pageModeCache.put(PdfPageMode.USE_ATTACHMENTS, PdfWriter.PageModeUseAttachments);
    }

    private static Map<PdfBooleanPreference, PdfName> booleanPrefCache = new HashMap<PdfBooleanPreference, PdfName>();
    static {
        booleanPrefCache.put(PdfBooleanPreference.HIDE_TOOLBAR, PdfName.HIDETOOLBAR);
        booleanPrefCache.put(PdfBooleanPreference.CENTER_WINDOW, PdfName.CENTERWINDOW);
        booleanPrefCache.put(PdfBooleanPreference.DISPLAY_DOC_TITLE, PdfName.DISPLAYDOCTITLE);
        booleanPrefCache.put(PdfBooleanPreference.FIT_WINDOW, PdfName.FITWINDOW);
        booleanPrefCache.put(PdfBooleanPreference.HIDE_MENUBAR, PdfName.HIDEMENUBAR);
        booleanPrefCache.put(PdfBooleanPreference.HIDE_WINDOW_UI, PdfName.HIDEWINDOWUI);
    }

    private ViewerPreferencesUtils() {
        // util
    }

    /**
     * Mapping between Sejda and iText direction constants
     * 
     * @param direction
     * @return the iText direction constant
     */
    public static PdfName getDirection(PdfDirection direction) {
        if (PdfDirection.RIGHT_TO_LEFT.equals(direction)) {
            return PdfName.R2L;
        }
        return PdfName.L2R;
    }

    /**
     * Mapping between Sejda and iText print scaling constants
     * 
     * @param scaling
     * @return the iText print scaling constant
     */
    public static PdfName getPrintScaling(PdfPrintScaling scaling) {
        if (PdfPrintScaling.NONE.equals(scaling)) {
            return PdfName.NONE;
        }
        return PdfName.APPDEFAULT;
    }

    /**
     * Mapping between Sejda and iText duplex constants
     * 
     * @param duplex
     * @return the iText duplex constant
     */
    public static PdfName getDuplex(PdfDuplex duplex) {
        return duplexCache.get(duplex);
    }

    /**
     * Mapping between Sejda and iText non full screen mode constants
     * 
     * @param nfsMode
     * @return the iText non full screen mode constant
     */
    public static PdfName getNFSMode(PdfNonFullScreenPageMode nfsMode) {
        return nfsModeCache.get(nfsMode);
    }

    /**
     * Mapping between Sejda and iText boolean preferences constants
     * 
     * @param booleanPref
     * @return the iText boolean preferencese constant
     */
    public static PdfName getBooleanPreference(PdfBooleanPreference booleanPref) {
        return booleanPrefCache.get(booleanPref);
    }

    /**
     * @param mode
     * @param layout
     * @return the int representing the ORed layout|mode that can be used to set the vewer preferences in the pdf stamper.
     */
    public static int getViewerPreferences(PdfPageMode mode, PdfPageLayout layout) {
        return pageModeCache.get(mode) | layoutCache.get(layout);
    }
}
