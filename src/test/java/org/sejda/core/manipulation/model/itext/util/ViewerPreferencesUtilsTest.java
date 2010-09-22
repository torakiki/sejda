/*
 * Created on 21/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.itext.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDuplex;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPrintScaling;
import org.sejda.core.manipulation.model.task.itext.util.ViewerPreferencesUtils;

import com.itextpdf.text.pdf.PdfName;

/**
 * @author Andrea Vacondio
 *
 */
public class ViewerPreferencesUtilsTest {

    @Test
    public void testGetDirection() {
        assertEquals(PdfName.L2R, ViewerPreferencesUtils.getDirection(PdfDirection.LEFT_TO_RIGHT));
        assertEquals(PdfName.R2L, ViewerPreferencesUtils.getDirection(PdfDirection.RIGHT_TO_LEFT));
    }

    @Test
    public void testGetDuplex() {
        assertEquals(PdfName.SIMPLEX, ViewerPreferencesUtils.getDuplex(PdfDuplex.SIMPLEX));
        assertEquals(PdfName.DUPLEXFLIPLONGEDGE, ViewerPreferencesUtils.getDuplex(PdfDuplex.DUPLEX_FLIP_LONG_EDGE));
        assertEquals(PdfName.DUPLEXFLIPSHORTEDGE, ViewerPreferencesUtils.getDuplex(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE));
    }

    @Test
    public void testGetPrintScaling() {
        assertEquals(PdfName.APPDEFAULT, ViewerPreferencesUtils.getPrintScaling(PdfPrintScaling.APP_DEFAULT));
        assertEquals(PdfName.NONE, ViewerPreferencesUtils.getPrintScaling(PdfPrintScaling.NONE));
    }

    @Test
    public void testGetNFSMode() {
        assertEquals(PdfName.USENONE, ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_NONE));
        assertEquals(PdfName.USEOC, ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_OC));
        assertEquals(PdfName.USEOUTLINES, ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_OUTLINES));
        assertEquals(PdfName.USETHUMBS, ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_THUMNS));
    }

    @Test
    public void testGetBooleanPref() {
        assertEquals(PdfName.CENTERWINDOW, ViewerPreferencesUtils
                .getBooleanPreference(PdfBooleanPreference.CENTER_WINDOW));
        assertEquals(PdfName.DISPLAYDOCTITLE, ViewerPreferencesUtils
                .getBooleanPreference(PdfBooleanPreference.DISPLAY_DOC_TITLE));
        assertEquals(PdfName.FITWINDOW, ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.FIT_WINDOW));
        assertEquals(PdfName.HIDEMENUBAR, ViewerPreferencesUtils
                .getBooleanPreference(PdfBooleanPreference.HIDE_MENUBAR));
        assertEquals(PdfName.HIDETOOLBAR, ViewerPreferencesUtils
                .getBooleanPreference(PdfBooleanPreference.HIDE_TOOLBAR));
        assertEquals(PdfName.HIDEWINDOWUI, ViewerPreferencesUtils
                .getBooleanPreference(PdfBooleanPreference.HIDE_WINDOW_UI));
    }
}
