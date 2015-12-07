/*
 * Created on 10/set/2011
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
import org.sejda.model.output.ExistingOutputPolicy;
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
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
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
