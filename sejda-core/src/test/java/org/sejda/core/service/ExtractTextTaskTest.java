/*
 * Created on 24/ago/2011
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.ExtractTextParameters;
import org.sejda.model.task.Task;

/**
 * Parent class for tests testing the ExtractText task.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class ExtractTextTaskTest implements TestableTask<ExtractTextParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private ExtractTextParameters parameters;
    private ByteArrayOutputStream out;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream();
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the unpack parameters
     * 
     */
    private void setUpParameters() {
        parameters = new ExtractTextParameters();
        parameters.setOutput(new StreamTaskOutput(out));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "enc_test_test_file.pdf");
        parameters.addSource(source);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setTextEncoding("UTF-8");
    }

    @Test
    public void executeUnethicalExtract() throws TaskException, IOException {
        new WithUnethicalReadProperty(true) {
            @Override
            public void execute() throws TaskException, IOException {
                when(context.getTask(parameters)).thenReturn((Task) getTask());
                victim.execute(parameters);
                ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
                ZipInputStream zip = new ZipInputStream(input);
                int counter = 0;
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {
                    counter++;
                    assertTrue(entry.getName().endsWith(SejdaFileExtensions.TXT_EXTENSION));
                    entry = zip.getNextEntry();
                }
                assertEquals(1, counter);
            }
        };
    }

    @Test
    public void failedExtractMissingPermission() throws TaskException, IOException {
        new WithUnethicalReadProperty(false) {
            @Override
            public void execute() throws TaskException {
                when(context.getTask(parameters)).thenReturn((Task) getTask());
                TestListenerFailed failListener = TestListenerFactory.newFailedListener();
                ThreadLocalNotificationContext.getContext().addListener(failListener);
                victim.execute(parameters);
                assertTrue(failListener.isFailed());
            }
        };
    }
}
