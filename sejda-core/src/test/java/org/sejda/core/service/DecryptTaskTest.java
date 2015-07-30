/*
 * Created on 15/set/2010
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
package org.sejda.core.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.Sejda;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.DecryptParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract test unit for the decrypt task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
@SuppressWarnings("unchecked")
public abstract class DecryptTaskTest extends PdfOutEnabledTest implements TestableTask<DecryptParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private DecryptParameters parameters = new DecryptParameters();

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setOverwrite(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
    }

    private void setUpInput() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_test_test_file.pdf", "test");
        parameters.addSource(source);
    }

    private void setUpInputNoPwd() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_empty_pwd.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_empty_pwd.pdf", "");
        parameters.addSource(source);
    }

    private void setUpInputSamePwd() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_usr_own_same_pwd.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_usr_own_same_pwd.pdf", "test");
        parameters.addSource(source);
    }

    private void setUpInputOwnerCompressed() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_owner_compressed.pdf");
        parameters.addSource(PdfStreamSource.newInstanceWithPassword(stream, "enc_owner_compressed.pdf", "test"));
    }

    private void setUpInputOwnerUncompressed() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_owner_uncompressed.pdf");
        parameters.addSource(PdfStreamSource.newInstanceWithPassword(stream, "enc_owner_uncompressed.pdf", "test"));
    }

    private void setUpInputOwnerCompressedNoPwd() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_owner_compressed.pdf");
        parameters.addSource(PdfStreamSource.newInstanceNoPassword(stream, "enc_owner_compressed.pdf"));
    }

    private void setUpInputOwnerUncompressedNoPwd() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_owner_uncompressed.pdf");
        parameters.addSource(PdfStreamSource.newInstanceNoPassword(stream, "enc_owner_uncompressed.pdf"));
    }

    @Test
    public void executeNoPwd() throws TaskException, IOException {
        setUpInputNoPwd();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_enc_empty_pwd.pdf");
        assertCommonsAndClose(reader);
    }

    @Test
    public void executeSamePwd() throws TaskException, IOException {
        setUpInputSamePwd();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_enc_usr_own_same_pwd.pdf");
        assertCommonsAndClose(reader);
    }

    @Test
    public void execute() throws TaskException, IOException {
        setUpInput();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_enc_test_test_file.pdf");
        assertCommonsAndClose(reader);
    }

    @Test
    public void executeOwnerCompressed() throws TaskException, IOException {
        setUpInputOwnerCompressed();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_enc_owner_compressed.pdf");
        assertCommonsAndClose(reader);
    }

    @Test
    public void executeOwnerUnompressed() throws TaskException, IOException {
        setUpInputOwnerUncompressed();
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_enc_owner_uncompressed.pdf");
        assertCommonsAndClose(reader);
    }

    @Test
    public void executeOwnerCompressedUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(true) {
            @Override public void execute() throws TaskException, IOException {
                setUpInputOwnerCompressedNoPwd();
                when(context.getTask(parameters)).thenReturn((Task) getTask());
                initializeNewStreamOutput(parameters);
                victim.execute(parameters);
                PdfReader reader = getReaderFromResultStream("test_prefix_enc_owner_compressed.pdf");
                assertCommonsAndClose(reader);
            }
        };
    }

    @Test
    public void failingExecuteOwnerCompresseNotUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(false) {
            @Override public void execute() throws TaskException, IOException {
                setUpInputOwnerCompressedNoPwd();
                when(context.getTask(parameters)).thenReturn((Task) getTask());
                initializeNewStreamOutput(parameters);
                TestListenerFailed failListener = TestListenerFactory.newFailedListener();
                ThreadLocalNotificationContext.getContext().addListener(failListener);
                victim.execute(parameters);
                assertTrue(failListener.isFailed());
            }
        };
    }

    @Test
    public void executeOwnerUnompressedUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(true) {
            @Override public void execute() throws TaskException, IOException {
                setUpInputOwnerUncompressedNoPwd();
                when(context.getTask(parameters)).thenReturn((Task) getTask());
                initializeNewStreamOutput(parameters);
                victim.execute(parameters);
                PdfReader reader = getReaderFromResultStream("test_prefix_enc_owner_uncompressed.pdf");
                assertCommonsAndClose(reader);
            }
        };
    }

    @Test
    public void failingExecuteOwnerUncompresseNotUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(false) {
            @Override public void execute() throws TaskException, IOException {
                setUpInputOwnerUncompressedNoPwd();
                when(context.getTask(parameters)).thenReturn((Task) getTask());
                initializeNewStreamOutput(parameters);
                TestListenerFailed failListener = TestListenerFactory.newFailedListener();
                ThreadLocalNotificationContext.getContext().addListener(failListener);
                victim.execute(parameters);
                assertTrue("Expected task to fail, it did not.", failListener.isFailed());
            }
        };
    }

    private void assertCommonsAndClose(PdfReader reader) {
        try {
            assertCreator(reader);
            assertFalse(reader.isEncrypted());
            assertVersion(reader, PdfVersion.VERSION_1_6);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
