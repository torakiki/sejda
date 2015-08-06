/*
 * Created on 21/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Utility methods related to the viewer preferences functionalities.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ViewerPreferencesUtils {

    private static final Map<PdfDuplex, PdfName> DUPLEX_CACHE;
    static {
        Map<PdfDuplex, PdfName> duplexCache = new HashMap<PdfDuplex, PdfName>();
        duplexCache.put(PdfDuplex.SIMPLEX, PdfName.SIMPLEX);
        duplexCache.put(PdfDuplex.DUPLEX_FLIP_LONG_EDGE, PdfName.DUPLEXFLIPLONGEDGE);
        duplexCache.put(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE, PdfName.DUPLEXFLIPSHORTEDGE);
        DUPLEX_CACHE = Collections.unmodifiableMap(duplexCache);
    }

    private static final Map<PdfNonFullScreenPageMode, PdfName> NFS_MODE_CACHE;
    static {
        Map<PdfNonFullScreenPageMode, PdfName> nfsModeCache = new HashMap<PdfNonFullScreenPageMode, PdfName>();
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_NONE, PdfName.USENONE);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_OC, PdfName.USEOC);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_OUTLINES, PdfName.USEOUTLINES);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_THUMNS, PdfName.USETHUMBS);
        NFS_MODE_CACHE = Collections.unmodifiableMap(nfsModeCache);
    }

    private static final Map<PdfPageLayout, Integer> LAYOUT_CACHE;
    static {
        Map<PdfPageLayout, Integer> layoutCache = new HashMap<PdfPageLayout, Integer>();
        layoutCache.put(PdfPageLayout.SINGLE_PAGE, PdfWriter.PageLayoutSinglePage);
        layoutCache.put(PdfPageLayout.ONE_COLUMN, PdfWriter.PageLayoutOneColumn);
        layoutCache.put(PdfPageLayout.TWO_COLUMN_LEFT, PdfWriter.PageLayoutTwoColumnLeft);
        layoutCache.put(PdfPageLayout.TWO_COLUMN_RIGHT, PdfWriter.PageLayoutTwoColumnRight);
        layoutCache.put(PdfPageLayout.TWO_PAGE_LEFT, PdfWriter.PageLayoutTwoPageLeft);
        layoutCache.put(PdfPageLayout.TWO_PAGE_RIGHT, PdfWriter.PageLayoutTwoPageRight);
        LAYOUT_CACHE = Collections.unmodifiableMap(layoutCache);
    }

    private static final Map<PdfPageMode, Integer> PAGE_MODE_CACHE;
    static {
        Map<PdfPageMode, Integer> pageModeCache = new HashMap<PdfPageMode, Integer>();
        pageModeCache.put(PdfPageMode.USE_NONE, PdfWriter.PageModeUseNone);
        pageModeCache.put(PdfPageMode.USE_THUMBS, PdfWriter.PageModeUseThumbs);
        pageModeCache.put(PdfPageMode.USE_OUTLINES, PdfWriter.PageModeUseOutlines);
        pageModeCache.put(PdfPageMode.FULLSCREEN, PdfWriter.PageModeFullScreen);
        pageModeCache.put(PdfPageMode.USE_OC, PdfWriter.PageModeUseOC);
        pageModeCache.put(PdfPageMode.USE_ATTACHMENTS, PdfWriter.PageModeUseAttachments);
        PAGE_MODE_CACHE = Collections.unmodifiableMap(pageModeCache);
    }

    private static final Map<PdfBooleanPreference, PdfName> BOOLEAN_PREF_CACHE;
    static {
        Map<PdfBooleanPreference, PdfName> booleanPrefCache = new HashMap<PdfBooleanPreference, PdfName>();
        booleanPrefCache.put(PdfBooleanPreference.HIDE_TOOLBAR, PdfName.HIDETOOLBAR);
        booleanPrefCache.put(PdfBooleanPreference.CENTER_WINDOW, PdfName.CENTERWINDOW);
        booleanPrefCache.put(PdfBooleanPreference.DISPLAY_DOC_TITLE, PdfName.DISPLAYDOCTITLE);
        booleanPrefCache.put(PdfBooleanPreference.FIT_WINDOW, PdfName.FITWINDOW);
        booleanPrefCache.put(PdfBooleanPreference.HIDE_MENUBAR, PdfName.HIDEMENUBAR);
        booleanPrefCache.put(PdfBooleanPreference.HIDE_WINDOW_UI, PdfName.HIDEWINDOWUI);
        BOOLEAN_PREF_CACHE = Collections.unmodifiableMap(booleanPrefCache);
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
        return DUPLEX_CACHE.get(duplex);
    }

    /**
     * Mapping between Sejda and iText non full screen mode constants
     * 
     * @param nfsMode
     * @return the iText non full screen mode constant
     */
    public static PdfName getNFSMode(PdfNonFullScreenPageMode nfsMode) {
        return NFS_MODE_CACHE.get(nfsMode);
    }

    /**
     * Mapping between Sejda and iText boolean preferences constants
     * 
     * @param booleanPref
     * @return the iText boolean preferences constant
     */
    public static PdfName getBooleanPreference(PdfBooleanPreference booleanPref) {
        return BOOLEAN_PREF_CACHE.get(booleanPref);
    }

    /**
     * Mapping between Sejda and iText page mode constants
     * 
     * @param mode
     * @return the iText page mode int
     */
    public static int getPageMode(PdfPageMode mode) {
        return PAGE_MODE_CACHE.get(mode);
    }

    /**
     * Mapping between Sejda and iText page layout constants
     * 
     * @param layout
     * @return the iText page layout int
     */
    public static int getPageLayout(PdfPageLayout layout) {
        return LAYOUT_CACHE.get(layout);
    }

    /**
     * @param mode
     * @param layout
     * @return the int representing the ORed layout|mode that can be used to set the viewer preferences in the pdf stamper.
     */
    public static int getViewerPreferences(PdfPageMode mode, PdfPageLayout layout) {
        return getPageMode(mode) | getPageLayout(layout);
    }
}
