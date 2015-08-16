/*
 * Created on 25/dic/2010
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
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract test unit for the alternate mix task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class AlternateMixTaskTest extends PdfOutEnabledTest implements TestableTask<AlternateMixParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private AlternateMixParameters parameters;

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the rotation parameters
     * 
     */
    private void setUpParameters() {
        InputStream firstStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource firstSource = PdfStreamSource.newInstanceNoPassword(firstStream, "first_test_file.pdf");
        PdfMixInput firstInput = new PdfMixInput(firstSource);
        InputStream secondStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource secondSource = PdfStreamSource.newInstanceNoPassword(secondStream, "first_test_file.pdf");
        PdfMixInput secondInput = new PdfMixInput(secondSource, true, 3);
        parameters = new AlternateMixParameters(firstInput, secondInput);
        parameters.setOutputName("outName.pdf");
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_5);
        assertEquals(8, reader.getNumberOfPages());
        reader.close();
    }

    protected AlternateMixParameters getParameters() {
        return parameters;
    }
}
