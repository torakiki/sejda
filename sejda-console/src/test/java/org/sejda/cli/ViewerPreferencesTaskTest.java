/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;

/**
 * Tests for the ViewerPreferences command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ViewerPreferencesTaskTest extends AbstractTaskTest {

    public ViewerPreferencesTaskTest() {
        super(StandardTestableTask.SET_VIEWER_PREFERENCES);
    }

    @Test
    public void onFlagOptions() {
        ViewerPreferencesParameters parameters = defaultCommandLine().withFlag("--centerWindow")
                .withFlag("--displayDocTitle").withFlag("--hideMenu").withFlag("--fitWindow")
                .withFlag("--hideWindowUI").withFlag("--hideToolbar").invokeSejdaConsole();

        assertContainsAll(EnumSet.allOf(PdfBooleanPreference.class), parameters.getEnabledPreferences());
    }

    @Test
    public void offFlagOptions() {
        ViewerPreferencesParameters parameters = defaultCommandLine().invokeSejdaConsole();

        assertTrue(parameters.getEnabledPreferences().isEmpty());
    }

    @Test
    public void specifiedValues() {
        ViewerPreferencesParameters parameters = defaultCommandLine().with("--printScaling", "app_default")
                .with("--direction", "r2l").with("--duplex", "duplex_flip_short_edge").with("--nfsMode", "nfsthumbs")
                .with("--layout", "twopagel").with("--mode", "attachments").invokeSejdaConsole();

        assertEquals(PdfPrintScaling.APP_DEFAULT, parameters.getPrintScaling());
        assertEquals(PdfDirection.RIGHT_TO_LEFT, parameters.getDirection());
        assertEquals(PdfDuplex.DUPLEX_FLIP_SHORT_EDGE, parameters.getDuplex());
        assertEquals(PdfNonFullScreenPageMode.USE_THUMNS, parameters.getNfsMode());
        assertEquals(PdfPageLayout.TWO_PAGE_LEFT, parameters.getPageLayout());
        assertEquals(PdfPageMode.USE_ATTACHMENTS, parameters.getPageMode());
    }

    @Test
    public void defaultValues() {
        ViewerPreferencesParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertNull(parameters.getPrintScaling());
        assertNull(parameters.getDirection());
        assertNull(parameters.getDuplex());
        assertEquals(PdfNonFullScreenPageMode.USE_NONE, parameters.getNfsMode());
        assertEquals(PdfPageLayout.SINGLE_PAGE, parameters.getPageLayout());
        assertEquals(PdfPageMode.USE_NONE, parameters.getPageMode());
    }
}
