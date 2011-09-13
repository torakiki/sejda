/*
 * Created on 30/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.pdfbox.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;

/**
 * @author Andrea Vacondio
 * 
 */
public class ViewerPreferencesUtilsTestTest {
    @Test
    public void testGetDirection() {
        assertEquals(PDViewerPreferences.READING_DIRECTION_L2R,
                ViewerPreferencesUtils.getDirection(PdfDirection.LEFT_TO_RIGHT));
        assertEquals(PDViewerPreferences.READING_DIRECTION_R2L,
                ViewerPreferencesUtils.getDirection(PdfDirection.RIGHT_TO_LEFT));
    }

    @Test
    @Ignore
    public void testGetDuplex() {
        // not yet supported by PDFBox
    }

    @Test
    @Ignore
    public void testGetPrintScaling() {
        // not yet supported by PDFBox
    }

    @Test
    public void testGetNFSMode() {
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_NONE,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_NONE));
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_OPTIONAL_CONTENT,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_OC));
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_OUTLINES,
                ViewerPreferencesUtils.getNFSMode(PdfNonFullScreenPageMode.USE_OUTLINES));
        assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_THUMBS,
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

    @Test(expected = TaskException.class)
    public void testSetBooleanPreferencesNullPref() throws TaskException {
        ViewerPreferencesUtils.setBooleanPreferences(null, Collections.EMPTY_SET);
    }

    @Test
    public void testGetPageMode() {
        assertEquals(PDDocumentCatalog.PAGE_MODE_FULL_SCREEN,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.FULLSCREEN));
        assertEquals(PDDocumentCatalog.PAGE_MODE_USE_ATTACHMENTS,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_ATTACHMENTS));
        assertEquals(PDDocumentCatalog.PAGE_MODE_USE_NONE, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_NONE));
        assertEquals(PDDocumentCatalog.PAGE_MODE_USE_OPTIONAL_CONTENT,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_OC));
        assertEquals(PDDocumentCatalog.PAGE_MODE_USE_OUTLINES,
                ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_OUTLINES));
        assertEquals(PDDocumentCatalog.PAGE_MODE_USE_THUMBS, ViewerPreferencesUtils.getPageMode(PdfPageMode.USE_THUMBS));
    }
}
