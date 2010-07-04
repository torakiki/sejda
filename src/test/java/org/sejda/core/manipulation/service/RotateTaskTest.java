/*
 * Created on 13/giu/2010
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
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.RotationParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.rotation.PageRotation;
import org.sejda.core.manipulation.model.rotation.Rotation;
import org.sejda.core.manipulation.model.rotation.RotationType;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.RotateTask;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Test unit for the rotate task
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class RotateTaskTest extends PdfStreamOutEnabledTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private RotationParameters parameters = new RotationParameters();
    private List<Task> tasks = new ArrayList<Task>();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        tasks.add(new RotateTask());
        victim.setContext(context);
    }

    /**
     * Set up of the rotation parameters
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setRotation(new PageRotation(Rotation.DEGREES_180, RotationType.ALL_PAGES));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = new PdfStreamSource(stream, "test_file.pdf");
        List<PdfSource> sourceList = new ArrayList<PdfSource>();
        sourceList.add(source);
        parameters.setInputList(sourceList);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        for (Task task : tasks) {
            when(context.getTask(parameters)).thenReturn(task);
            initializeNewStreamOutput(parameters);
            victim.execute(parameters);
            PdfReader reader = getReaderFromResult("test_prefix_test_file.pdf");
            assertEquals(4, reader.getNumberOfPages());
            assertEquals(180, reader.getPageRotation(2));
        }
    }
}
