/*
 * Created on 28/mag/2010
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.core.Sejda;
import org.sejda.core.context.SejdaConfiguration;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.model.task.TaskExecutionContext;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unit for the {@link DefaultTaskExecutionService}
 *
 * @author Andrea Vacondio
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultTaskExecutionServiceTest {

    private DefaultTaskExecutionService victim;
    private TestTaskParameter parameters = new TestTaskParameter();
    private Task task = mock(Task.class);
    @TempDir
    public Path folder;

    @BeforeEach
    public void setUp() throws TaskException {
        System.setProperty(Sejda.USER_CONFIG_FILE_PROPERTY_NAME, "sejda-test.xml");
        parameters.setOutput(new FileTaskOutput(folder.resolve("out.pdf").toFile()));
        var configuration = mock(SejdaConfiguration.class);
        when(configuration.getTask(any(TaskParameters.class))).thenReturn(task);
        when(configuration.isValidation()).thenReturn(Boolean.TRUE);
        victim = new DefaultTaskExecutionService(configuration);
    }

    @Test
    public void testExecute() {
        EventListener<TaskExecutionStartedEvent> listener = mock(EventListener.class);
        GlobalNotificationContext.getContext().addListener(TaskExecutionStartedEvent.class, listener);
        victim.execute(parameters);
        verify(listener).onEvent(any());
        GlobalNotificationContext.getContext().clearListeners();
    }

    @Test
    public void testInvalidParameters() throws TaskException {
        parameters.setVersion(PdfVersion.VERSION_1_4);
        parameters.setCompress(true);
        victim.execute(parameters);
        verify(task, never()).before(eq(parameters), any());
        verify(task, never()).after();
        verify(task, never()).execute(parameters);
    }

    @Test
    public void testNegativeBeforeExecution() throws TaskException {
        doThrow(new TaskExecutionException("Mock exception")).when(task)
                .before(any(TaskParameters.class), any(TaskExecutionContext.class));
        SingleTaskOutput output = mock(SingleTaskOutput.class);
        parameters.setOutput(output);
        victim.execute(parameters);
        verify(task).before(eq(parameters), any());
        verify(task).after();
        verify(task, never()).execute(parameters);
    }

}
