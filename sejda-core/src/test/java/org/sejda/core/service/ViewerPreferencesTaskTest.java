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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PageLayout;
import org.sejda.sambox.pdmodel.PageMode;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.DUPLEX;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.PRINT_SCALING;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.READING_DIRECTION;

/**
 * test unit for the viewer preferences task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class ViewerPreferencesTaskTest extends BaseTaskTest<ViewerPreferencesParameters> {
    private ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();

    private void setUpParams(PdfSource<?> source) throws IOException {
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_7);
        parameters.setDirection(PdfDirection.LEFT_TO_RIGHT);
        parameters.setDuplex(PdfDuplex.SIMPLEX);
        parameters.setNfsMode(PdfNonFullScreenPageMode.USE_THUMNS);
        parameters.setPageLayout(PdfPageLayout.ONE_COLUMN);
        parameters.setPageMode(PdfPageMode.USE_THUMBS);
        parameters.setPrintScaling(PdfPrintScaling.APP_DEFAULT);
        parameters.addEnabledPreference(PdfBooleanPreference.CENTER_WINDOW);
        parameters.addEnabledPreference(PdfBooleanPreference.HIDE_MENUBAR);
        parameters.addSource(source);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
    }

    @Test
    public void testExecute() throws IOException {
        setUpParams(shortInput());
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws IOException {
        setUpParams(stronglyEncryptedInput());
        doExecute();
    }

    private void doExecute() throws IOException {
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_7).forEachPdfOutput(d -> {
            PDDocumentCatalog catalog = d.getDocumentCatalog();
            PDViewerPreferences prefs = catalog.getViewerPreferences();
            assertTrue(prefs.hideMenubar());
            assertTrue(prefs.centerWindow());
            assertFalse(prefs.hideToolbar());
            assertEquals(DUPLEX.Simplex.toString(), prefs.getDuplex());
            assertEquals(NON_FULL_SCREEN_PAGE_MODE.UseThumbs.toString(), prefs.getNonFullScreenPageMode());
            assertEquals(PRINT_SCALING.AppDefault.toString(), prefs.getPrintScaling());
            assertEquals(READING_DIRECTION.L2R.toString(), prefs.getReadingDirection());
            assertEquals(PageLayout.ONE_COLUMN, catalog.getPageLayout());
            assertEquals(PageMode.USE_THUMBS, catalog.getPageMode());
        });
    }
}
