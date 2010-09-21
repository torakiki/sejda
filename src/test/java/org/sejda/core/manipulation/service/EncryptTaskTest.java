/*
 * Created on 17/set/2010
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.pdf.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.PdfEncryption;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.EncryptTask;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Test unit for the encrypt task
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class EncryptTaskTest extends PdfStreamOutEnabledTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private EncryptParameters parameters = new EncryptParameters();
    private List<Task> tasks = new ArrayList<Task>();

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        tasks.add(new EncryptTask());
        victim.setContext(context);
    }

    /**
     * Set up of the rotation parameters
     */
    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = new PdfStreamSource(stream, "test_file.pdf");
        source.setPassword("test");
        parameters.setEncryptionAlgorithm(PdfEncryption.AES_ENC_128);
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteOwner() throws TaskException, IOException {
        parameters.setOwnerPassword("test");
        parameters.addPermission(PdfAccessPermission.COPY);
        for (Task task : tasks) {
            when(context.getTask(parameters)).thenReturn(task);
            initializeNewStreamOutput(parameters);
            victim.execute(parameters);
            PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf", "test".getBytes());
            assertCreator(reader);
            assertTrue(reader.isEncrypted());
            assertTrue((reader.getPermissions() & PdfWriter.ALLOW_COPY) == PdfWriter.ALLOW_COPY);
            assertFalse((reader.getPermissions() & PdfWriter.ALLOW_ASSEMBLY) == PdfWriter.ALLOW_ASSEMBLY);
            reader.close();
        }
    }
}
