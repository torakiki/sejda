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
package org.sejda.core.manipulation.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.TaskException;
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

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set metadata parameters
     * 
     */
    private void setUpParameters() {
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
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_file.pdf");
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
