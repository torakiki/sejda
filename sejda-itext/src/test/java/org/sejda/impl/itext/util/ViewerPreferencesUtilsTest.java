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
package org.sejda.impl.itext.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.impl.itext.util.ViewerPreferencesUtils;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfWriter;

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
        PdfName duplex = ViewerPreferencesUtils.getDuplex(PdfDuplex.SIMPLEX);
        assertEquals(PdfName.SIMPLEX, duplex);
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
        assertEquals(PdfName.CENTERWINDOW,
                ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.CENTER_WINDOW));
        assertEquals(PdfName.DISPLAYDOCTITLE,
                ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.DISPLAY_DOC_TITLE));
        assertEquals(PdfName.FITWINDOW, ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.FIT_WINDOW));
        assertEquals(PdfName.HIDEMENUBAR,
                ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.HIDE_MENUBAR));
        assertEquals(PdfName.HIDETOOLBAR,
                ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.HIDE_TOOLBAR));
        assertEquals(PdfName.HIDEWINDOWUI,
                ViewerPreferencesUtils.getBooleanPreference(PdfBooleanPreference.HIDE_WINDOW_UI));
    }

    @Test
    public void testGetPageMode() {
        assertEquals(PdfWriter.PageModeFullScreen, ViewerPreferencesUtils.getPageMode(PdfPageMode.FULLSCREEN));
        assertEquals(PdfWriter.PageModeUseAttachments, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_ATTACHMENTS));
        assertEquals(PdfWriter.PageModeUseNone, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_NONE));
        assertEquals(PdfWriter.PageModeUseOC, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_OC));
        assertEquals(PdfWriter.PageModeUseOutlines, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_OUTLINES));
        assertEquals(PdfWriter.PageModeUseThumbs, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_THUMBS));
    }
}
