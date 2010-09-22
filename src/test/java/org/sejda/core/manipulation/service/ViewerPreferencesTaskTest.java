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
package org.sejda.core.manipulation.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.ViewerPreferencesParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDuplex;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageLayout;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPrintScaling;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.ViewerPreferencesTask;
import org.sejda.core.manipulation.model.task.itext.util.ViewerPreferencesUtils;

import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

/**
 * test unit for the viewer preferences task
 * 
 * @author Andrea Vacondio
 * 
 */
public class ViewerPreferencesTaskTest extends PdfStreamOutEnabledTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();
    private List<Task> tasks = new ArrayList<Task>();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        tasks.add(new ViewerPreferencesTask());
        victim.setContext(context);
    }

    /**
     * Set up of the set metadata parameters
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setDirection(PdfDirection.LEFT_TO_RIGHT);
        parameters.setDuplex(PdfDuplex.SIMPLEX);
        parameters.setNfsMode(PdfNonFullScreenPageMode.USE_THUMNS);
        parameters.setPageLayout(PdfPageLayout.ONE_COLUMN);
        parameters.setPageMode(PdfPageMode.USE_THUMBS);
        parameters.setPrintScaling(PdfPrintScaling.APP_DEFAULT);
        parameters.addActivePreference(PdfBooleanPreference.CENTER_WINDOW);
        parameters.addActivePreference(PdfBooleanPreference.HIDE_MENUBAR);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = new PdfStreamSource(stream, "test_file.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException {
        for (Task task : tasks) {
            when(context.getTask(parameters)).thenReturn(task);
            initializeNewStreamOutput(parameters);
            victim.execute(parameters);
            PdfReader reader = getReaderFromResultStream("test_file.pdf");
            assertCreator(reader);
            assertEquals(ViewerPreferencesUtils.getViewerPreferences(PdfPageMode.USE_THUMBS, PdfPageLayout.ONE_COLUMN),
                    reader.getSimpleViewerPreferences());
            PdfDictionary catalog = (PdfDictionary) reader.getCatalog().get(PdfName.VIEWERPREFERENCES);
            assertEquals(PdfName.SIMPLEX, catalog.getAsName(PdfName.DUPLEX));
            assertEquals(PdfName.L2R, catalog.getAsName(PdfName.DIRECTION));
            assertEquals(PdfName.APPDEFAULT, catalog.getAsName(PdfName.PRINTSCALING));
            assertEquals(PdfName.USETHUMBS, catalog.getAsName(PdfName.NONFULLSCREENPAGEMODE));
            assertEquals(PdfBoolean.PDFTRUE, catalog.getAsBoolean(PdfName.CENTERWINDOW));
            assertEquals(PdfBoolean.PDFTRUE, catalog.getAsBoolean(PdfName.HIDEMENUBAR));
            assertEquals(PdfBoolean.PDFFALSE, catalog.getAsBoolean(PdfName.HIDETOOLBAR));
            reader.close();
        }
    }

}
