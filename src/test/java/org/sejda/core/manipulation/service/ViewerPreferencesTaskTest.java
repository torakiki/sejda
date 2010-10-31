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
@Ignore
public abstract class ViewerPreferencesTaskTest extends PdfStreamOutEnabledTest implements
        TestableTask<ViewerPreferencesParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
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
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_file.pdf");
        assertCreator(reader);
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
