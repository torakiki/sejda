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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
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

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;

/**
 * test unit for the viewer preferences task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class ViewerPreferencesTaskTest extends PdfOutEnabledTest implements
        TestableTask<ViewerPreferencesParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParams(PdfSource<?> source) {
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
        parameters.setOverwrite(true);
    }

    private void setUpParameters() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        setUpParams(source);
    }

    private void setUpParametersEncrypted() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "test_file.pdf", "test");
        setUpParams(source);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        setUpParameters();
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        setUpParametersEncrypted();
        doExecute();
    }

    public void doExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultZipStream("test_file.pdf");
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_7);
        PdfDictionary catalog = PdfViewerPreferencesImp.getViewerPreferences(reader.getCatalog())
                .getViewerPreferences();
        assertEquals(PdfName.SIMPLEX, catalog.getAsName(PdfName.DUPLEX));
        assertEquals(PdfName.L2R, catalog.getAsName(PdfName.DIRECTION));
        assertEquals(PdfName.APPDEFAULT, catalog.getAsName(PdfName.PRINTSCALING));
        assertEquals(PdfName.USETHUMBS, catalog.getAsName(PdfName.NONFULLSCREENPAGEMODE));
        assertEquals(PdfBoolean.PDFTRUE, catalog.getAsBoolean(PdfName.CENTERWINDOW));
        assertEquals(PdfBoolean.PDFTRUE, catalog.getAsBoolean(PdfName.HIDEMENUBAR));
        assertEquals(PdfBoolean.PDFFALSE, catalog.getAsBoolean(PdfName.HIDETOOLBAR));
        reader.close();
    }

    protected ViewerPreferencesParameters getParameters() {
        return parameters;
    }

}
