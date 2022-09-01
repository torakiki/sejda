/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;
import org.sejda.sambox.pdmodel.PageLayout;
import org.sejda.sambox.pdmodel.PageMode;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.DUPLEX;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.PRINT_SCALING;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.READING_DIRECTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods related to the viewer preferences functionalities.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ViewerPreferencesUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ViewerPreferencesUtils.class);

    private ViewerPreferencesUtils() {
        // hide utility constructor
    }

    private static final Map<PdfNonFullScreenPageMode, NON_FULL_SCREEN_PAGE_MODE> NFS_MODE_CACHE;
    static {
        Map<PdfNonFullScreenPageMode, NON_FULL_SCREEN_PAGE_MODE> nfsModeCache = new HashMap<>();
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_NONE, PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseNone);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_OC, PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseOC);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_OUTLINES,
                PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseOutlines);
        nfsModeCache.put(PdfNonFullScreenPageMode.USE_THUMNS, PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseThumbs);
        NFS_MODE_CACHE = Collections.unmodifiableMap(nfsModeCache);
    }

    private static final Map<PdfPageLayout, PageLayout> LAYOUT_CACHE;
    static {
        Map<PdfPageLayout, PageLayout> layoutCache = new HashMap<>();
        layoutCache.put(PdfPageLayout.SINGLE_PAGE, PageLayout.SINGLE_PAGE);
        layoutCache.put(PdfPageLayout.ONE_COLUMN, PageLayout.ONE_COLUMN);
        layoutCache.put(PdfPageLayout.TWO_COLUMN_LEFT, PageLayout.TWO_COLUMN_LEFT);
        layoutCache.put(PdfPageLayout.TWO_COLUMN_RIGHT, PageLayout.TWO_COLUMN_RIGHT);
        layoutCache.put(PdfPageLayout.TWO_PAGE_LEFT, PageLayout.TWO_PAGE_LEFT);
        layoutCache.put(PdfPageLayout.TWO_PAGE_RIGHT, PageLayout.TWO_PAGE_RIGHT);
        LAYOUT_CACHE = Collections.unmodifiableMap(layoutCache);
    }

    private static final Map<PdfPageMode, PageMode> PAGE_MODE_CACHE;
    static {
        Map<PdfPageMode, PageMode> pageModeCache = new HashMap<>();
        pageModeCache.put(PdfPageMode.USE_NONE, PageMode.USE_NONE);
        pageModeCache.put(PdfPageMode.USE_THUMBS, PageMode.USE_THUMBS);
        pageModeCache.put(PdfPageMode.USE_OUTLINES, PageMode.USE_OUTLINES);
        pageModeCache.put(PdfPageMode.FULLSCREEN, PageMode.FULL_SCREEN);
        pageModeCache.put(PdfPageMode.USE_OC, PageMode.USE_OPTIONAL_CONTENT);
        pageModeCache.put(PdfPageMode.USE_ATTACHMENTS, PageMode.USE_ATTACHMENTS);
        PAGE_MODE_CACHE = Collections.unmodifiableMap(pageModeCache);
    }

    private static final Map<PdfDuplex, DUPLEX> DUPLEX_CACHE;
    static {
        Map<PdfDuplex, DUPLEX> duplexCache = new HashMap<>();
        duplexCache.put(PdfDuplex.SIMPLEX, PDViewerPreferences.DUPLEX.Simplex);
        duplexCache.put(PdfDuplex.DUPLEX_FLIP_LONG_EDGE, PDViewerPreferences.DUPLEX.DuplexFlipLongEdge);
        duplexCache.put(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE, PDViewerPreferences.DUPLEX.DuplexFlipShortEdge);
        DUPLEX_CACHE = Collections.unmodifiableMap(duplexCache);
    }

    /**
     * Mapping between Sejda and PDFBox non full screen mode constants
     * 
     * @param nfsMode
     * @return the PDFBox non full screen mode constant.
     */
    public static NON_FULL_SCREEN_PAGE_MODE getNFSMode(PdfNonFullScreenPageMode nfsMode) {
        return NFS_MODE_CACHE.get(nfsMode);
    }

    /**
     * Mapping between Sejda and PDFBox page mode constants.
     * 
     * @param mode
     * @return the PDFBox page mode String constant.
     */
    public static PageMode getPageMode(PdfPageMode mode) {
        return PAGE_MODE_CACHE.get(mode);
    }

    /**
     * Mapping between Sejda and PDFBox page layout constants.
     * 
     * @param layout
     * @return the PDFBox page layout String constant.
     */
    public static PageLayout getPageLayout(PdfPageLayout layout) {
        return LAYOUT_CACHE.get(layout);
    }

    /**
     * Mapping between Sejda and PDFBox direction constants.
     * 
     * @param direction
     * @return the PDFBox direction constant
     */
    public static READING_DIRECTION getDirection(PdfDirection direction) {
        if (PdfDirection.RIGHT_TO_LEFT.equals(direction)) {
            return PDViewerPreferences.READING_DIRECTION.R2L;
        }
        return PDViewerPreferences.READING_DIRECTION.L2R;
    }

    /**
     * Mapping between Sejda and PDFBox duplex constants
     * 
     * @param duplex
     * @return the PDFBox duplex constant
     */
    public static DUPLEX getDuplex(PdfDuplex duplex) {
        return DUPLEX_CACHE.get(duplex);
    }

    /**
     * Mapping between Sejda and PDFBox print scaling constants
     * 
     * @param scaling
     * @return the PDFBox print scaling constant
     */
    public static PRINT_SCALING getPrintScaling(PdfPrintScaling scaling) {
        if (PdfPrintScaling.NONE.equals(scaling)) {
            return PDViewerPreferences.PRINT_SCALING.None;
        }
        return PDViewerPreferences.PRINT_SCALING.AppDefault;
    }

    /**
     * Enables the given set of boolean preferences on the given preferences instance and disables the others.
     * 
     * @param preferences
     * @param enabled
     * @throws TaskException
     *             if the given preferences instance is null.
     */
    public static void setBooleanPreferences(PDViewerPreferences preferences, Set<PdfBooleanPreference> enabled)
            throws TaskException {
        if (preferences == null) {
            throw new TaskException("Unable to set preferences on a null instance.");
        }
        for (PdfBooleanPreference current : PdfBooleanPreference.values()) {
            if (enabled.contains(current)) {
                PDFBoxActivableBooleanPreference.valueFromPdfBooleanPreference(current).enable(preferences);
                LOG.trace("{} = enabled.", current);
            } else {
                PDFBoxActivableBooleanPreference.valueFromPdfBooleanPreference(current).disable(preferences);
                LOG.trace("{} = disabled.", current);
            }
        }
    }

    /**
     * enum mapping from Sejda boolean preferences to an enam capable of activating boolean preferences on a PDFBox {@link PDViewerPreferences}.
     *
     * @author Andrea Vacondio
     */
    private enum PDFBoxActivableBooleanPreference {
        HIDE_TOOLBAR(PdfBooleanPreference.HIDE_TOOLBAR) {
            @Override
            void enable(PDViewerPreferences preferences) {
                preferences.setHideToolbar(true);
            }

            @Override
            void disable(PDViewerPreferences preferences) {
                preferences.setHideToolbar(false);
            }
        },
        HIDE_MENUBAR(PdfBooleanPreference.HIDE_MENUBAR) {
            @Override
            void enable(PDViewerPreferences preferences) {
                preferences.setHideMenubar(true);
            }

            @Override
            void disable(PDViewerPreferences preferences) {
                preferences.setHideMenubar(false);
            }
        },
        HIDE_WINDOW_UI(PdfBooleanPreference.HIDE_WINDOW_UI) {
            @Override
            void enable(PDViewerPreferences preferences) {
                preferences.setHideWindowUI(true);
            }

            @Override
            void disable(PDViewerPreferences preferences) {
                preferences.setHideWindowUI(false);
            }
        },
        FIT_WINDOW(PdfBooleanPreference.FIT_WINDOW) {
            @Override
            void enable(PDViewerPreferences preferences) {
                preferences.setFitWindow(true);
            }

            @Override
            void disable(PDViewerPreferences preferences) {
                preferences.setFitWindow(false);
            }
        },
        CENTER_WINDOW(PdfBooleanPreference.CENTER_WINDOW) {
            @Override
            void enable(PDViewerPreferences preferences) {
                preferences.setCenterWindow(true);
            }

            @Override
            void disable(PDViewerPreferences preferences) {
                preferences.setCenterWindow(false);
            }
        },
        DISPLAY_DOC_TITLE(PdfBooleanPreference.DISPLAY_DOC_TITLE) {
            @Override
            void enable(PDViewerPreferences preferences) {
                preferences.setDisplayDocTitle(true);
            }

            @Override
            void disable(PDViewerPreferences preferences) {
                preferences.setDisplayDocTitle(false);
            }
        };

        private final PdfBooleanPreference preference;

        PDFBoxActivableBooleanPreference(PdfBooleanPreference preference) {
            this.preference = preference;
        }

        /**
         * enables the boolean preference on the given preferences object;
         * 
         * @param preferences
         */
        abstract void enable(PDViewerPreferences preferences);

        /**
         * disable the boolean preference on the given preferences object;
         * 
         * @param preferences
         */
        abstract void disable(PDViewerPreferences preferences);

        static PDFBoxActivableBooleanPreference valueFromPdfBooleanPreference(PdfBooleanPreference pref) {
            for (PDFBoxActivableBooleanPreference current : PDFBoxActivableBooleanPreference.values()) {
                if (current.preference == pref) {
                    return current;
                }
            }
            throw new IllegalArgumentException(String.format("No activable preference found for %s", pref));
        }
    }
}
