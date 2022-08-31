/*
 * Created on 29/ago/2011
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PageLayout;
import org.sejda.sambox.pdmodel.PageMode;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

/**
 * @author Andrea Vacondio
 */
public class ViewerPreferencesSamboxTaskTest extends BaseTaskTest<ViewerPreferencesParameters> {
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

    @Test
    public void specificResultFilenames() throws IOException {
        setUpParams(shortInput());
        parameters.addSource(regularInput());
        parameters.addSource(mediumInput());
        parameters.addSource(mediumInput());
        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4)
                .assertOutputContainsFilenames("one.pdf", "two.pdf", "medium-test-file.pdf", "medium-test-file(1).pdf");
    }

    private void doExecute() throws IOException {
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_7).forEachPdfOutput(d -> {
            PDDocumentCatalog catalog = d.getDocumentCatalog();
            PDViewerPreferences prefs = catalog.getViewerPreferences();
            Assertions.assertTrue(prefs.hideMenubar());
            Assertions.assertTrue(prefs.centerWindow());
            Assertions.assertFalse(prefs.hideToolbar());
            Assertions.assertEquals(PDViewerPreferences.DUPLEX.Simplex.toString(), prefs.getDuplex());
            Assertions.assertEquals(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE.UseThumbs.toString(),
                    prefs.getNonFullScreenPageMode());
            Assertions.assertEquals(PDViewerPreferences.PRINT_SCALING.AppDefault.toString(), prefs.getPrintScaling());
            Assertions.assertEquals(PDViewerPreferences.READING_DIRECTION.L2R.toString(), prefs.getReadingDirection());
            Assertions.assertEquals(PageLayout.ONE_COLUMN, catalog.getPageLayout());
            Assertions.assertEquals(PageMode.USE_THUMBS, catalog.getPageMode());
        });
    }

    @Override
    public Task<ViewerPreferencesParameters> getTask() {
        return new ViewerPreferencesTask();
    }

}
