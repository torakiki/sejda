/*
 * Created on 30/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;
import org.sejda.sambox.pdmodel.PageMode;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andrea Vacondio
 * 
 */
public class ViewerPreferencesUtilsTest {
    @Test
    public void testGetDirection() {
        assertEquals(PDViewerPreferences.READING_DIRECTION.L2R,
                ViewerPreferencesUtils.getDirection(PdfDirection.LEFT_TO_RIGHT));
        assertEquals(PDViewerPreferences.READING_DIRECTION.R2L,
                ViewerPreferencesUtils.getDirection(PdfDirection.RIGHT_TO_LEFT));
    }

    @Test
    public void testGetDuplex() {
        assertEquals(PDViewerPreferences.DUPLEX.Simplex, ViewerPreferencesUtils.getDuplex(PdfDuplex.SIMPLEX));
        assertEquals(PDViewerPreferences.DUPLEX.DuplexFlipLongEdge,
                ViewerPreferencesUtils.getDuplex(PdfDuplex.DUPLEX_FLIP_LONG_EDGE));
        assertEquals(PDViewerPreferences.DUPLEX.DuplexFlipShortEdge,
                ViewerPreferencesUtils.getDuplex(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE));
    }

    @Test
    public void testGetPrintScaling() {
        assertEquals(PDViewerPreferences.PRINT_SCALING.None,
                ViewerPreferencesUtils.getPrintScaling(PdfPrintScaling.NONE));
        assertEquals(PDViewerPreferences.PRINT_SCALING.AppDefault,
                ViewerPreferencesUtils.getPrintScaling(PdfPrintScaling.APP_DEFAULT));
    }

    @Test
    public void testGetNFSMode() {
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseNone,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_NONE));
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseOC,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_OC));
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseOutlines,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_OUTLINES));
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseThumbs,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_THUMNS));
    }

    @Test
    public void testSetBooleanPreferences() throws TaskException {
        PDViewerPreferences preferences = mock(PDViewerPreferences.class);
        Set<PdfBooleanPreference> enabled = Collections.singleton(PdfBooleanPreference.CENTER_WINDOW);
        ViewerPreferencesUtils.setBooleanPreferences(preferences, enabled);
        verify(preferences).setCenterWindow(true);
        verify(preferences).setHideMenubar(false);
        verify(preferences).setHideToolbar(false);
        verify(preferences).setHideWindowUI(false);
        verify(preferences).setDisplayDocTitle(false);
        verify(preferences).setFitWindow(false);
    }

    @Test
    public void testSetBooleanPreferencesNullPref() throws TaskException {
        assertThrows(TaskException.class,
                () -> ViewerPreferencesUtils.setBooleanPreferences(null, Collections.EMPTY_SET));
    }

    @Test
    public void testGetPageMode() {
        assertEquals(PageMode.FULL_SCREEN,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.FULLSCREEN));
        assertEquals(PageMode.USE_ATTACHMENTS,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_ATTACHMENTS));
        assertEquals(PageMode.USE_NONE, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_NONE));
        assertEquals(PageMode.USE_OPTIONAL_CONTENT,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_OC));
        assertEquals(PageMode.USE_OUTLINES,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_OUTLINES));
        assertEquals(PageMode.USE_THUMBS, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_THUMBS));
    }
}
