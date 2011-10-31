/*
 * Created on 10/set/2011
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.RectangularBox;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class CropTaskTest extends PdfOutEnabledTest implements TestableTask<CropParameters> {

    private static final RectangularBox EVEN_PAGES_RECTANGLE = RectangularBox.newInstanceFromPoints(new Point(0, 0),
            new Point(595, 421));
    private static final RectangularBox ODD_PAGES_RECTANGLE = RectangularBox.newInstanceFromPoints(new Point(0, 421),
            new Point(595, 842));

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private CropParameters parameters;

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set page labels parameters
     * 
     */
    private void setUpParameters() {
        parameters = new CropParameters();
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addCropArea(ODD_PAGES_RECTANGLE);
        parameters.addCropArea(EVEN_PAGES_RECTANGLE);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_6);
        assertEquals(8, reader.getNumberOfPages());
        for (int i = 1; i <= reader.getNumberOfPages(); i = i + 2) {
            Rectangle crop = reader.getBoxSize(i, "crop");
            assertEqualsRectangleOddPages(ODD_PAGES_RECTANGLE, crop);
            Rectangle media = reader.getBoxSize(i, "media");
            assertEqualsRectangleOddPages(ODD_PAGES_RECTANGLE, media);
        }
        for (int i = 2; i <= reader.getNumberOfPages(); i = i + 2) {
            Rectangle crop = reader.getBoxSize(i, "crop");
            assertEqualsRectangleOddPages(EVEN_PAGES_RECTANGLE, crop);
            Rectangle media = reader.getBoxSize(i, "media");
            assertEqualsRectangleOddPages(EVEN_PAGES_RECTANGLE, media);
        }
        reader.close();
    }

    private void assertEqualsRectangleOddPages(RectangularBox expected, Rectangle found) {
        assertEquals(expected.getLeft(), (int) found.getLeft());
        assertEquals(expected.getBottom(), (int) found.getBottom());
        assertEquals(expected.getRight(), (int) found.getRight());
        assertEquals(expected.getTop(), (int) found.getTop());
    }

    protected CropParameters getParameters() {
        return parameters;
    }
}
