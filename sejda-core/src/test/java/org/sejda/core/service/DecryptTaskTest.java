/*
 * Created on 15/set/2010
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.Sejda;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.DecryptParameters;
import org.sejda.model.pdf.PdfVersion;

/**
 * Abstract test unit for the decrypt task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class DecryptTaskTest extends BaseTaskTest<DecryptParameters> {
    private DecryptParameters parameters;

    private void setUpParameters() {
        parameters = new DecryptParameters();
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.SKIP);
        parameters.setVersion(PdfVersion.VERSION_1_6);
    }

    @Test
    public void executeNoPwd() throws IOException {
        setUpParameters();
        parameters.addSource(customEncryptedInput("pdf/enc_test_test_file.pdf", "test"));
        parameters.addSource(customEncryptedInput("pdf/enc_empty_pwd.pdf", ""));
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        asserts(2);
    }

    @Test
    public void executeSamePwd() throws IOException {
        setUpParameters();
        parameters.addSource(customEncryptedInput("pdf/enc_usr_own_same_pwd.pdf", "test"));
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        asserts(1);
    }

    @Test
    public void execute() throws IOException {
        setUpParameters();
        parameters.addSource(customEncryptedInput("pdf/enc_test_test_file.pdf", "test"));
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        asserts(1);
    }

    @Test
    public void executeOwnerCompressed() throws IOException {
        setUpParameters();
        parameters.addSource(customEncryptedInput("pdf/enc_owner_compressed.pdf", "test"));
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        asserts(1);
    }

    @Test
    public void executeOwnerUnompressed() throws IOException {
        setUpParameters();
        parameters.addSource(customEncryptedInput("pdf/enc_owner_uncompressed.pdf", "test"));
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        asserts(1);
    }

    @Test
    public void executeOwnerCompressedUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(true) {
            @Override
            public void execute() throws IOException {
                setUpParameters();
                parameters.addSource(customInput("pdf/enc_owner_compressed.pdf"));
                testContext.directoryOutputTo(parameters);
                DecryptTaskTest.this.execute(parameters);
                asserts(1);
            }
        };
    }

    @Test
    public void failingExecuteOwnerCompresseNotUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(false) {
            @Override
            public void execute() throws IOException {
                setUpParameters();
                parameters.addSource(customInput("pdf/enc_owner_compressed.pdf"));
                testContext.directoryOutputTo(parameters);
                TestListenerFailed failListener = TestListenerFactory.newFailedListener();
                ThreadLocalNotificationContext.getContext().addListener(failListener);
                DecryptTaskTest.this.execute(parameters);
                assertTrue(failListener.isFailed());
            }
        };
    }

    @Test
    public void executeOwnerUnompressedUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(true) {
            @Override
            public void execute() throws IOException {
                setUpParameters();
                parameters.addSource(customInput("pdf/enc_owner_uncompressed.pdf"));
                testContext.directoryOutputTo(parameters);
                DecryptTaskTest.this.execute(parameters);
                asserts(1);
            }
        };
    }

    @Test
    public void failingExecuteOwnerUncompresseNotUnethical() throws TaskException, IOException {
        new WithUnethicalReadProperty(false) {
            @Override
            public void execute() throws IOException {
                setUpParameters();
                parameters.addSource(customInput("pdf/enc_owner_uncompressed.pdf"));
                testContext.directoryOutputTo(parameters);
                TestListenerFailed failListener = TestListenerFactory.newFailedListener();
                ThreadLocalNotificationContext.getContext().addListener(failListener);
                DecryptTaskTest.this.execute(parameters);
                assertTrue("Expected task to fail, it did not.", failListener.isFailed());
            }
        };
    }

    private void asserts(int size) throws IOException {
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(size);
        testContext.forEachPdfOutput(d -> {
            assertEquals(Sejda.CREATOR, d.getDocumentInformation().getCreator());
            assertFalse(d.isEncrypted());
            assertEquals("Wrong output PDF version", PdfVersion.VERSION_1_6.getVersionString(), d.getVersion());
        });
    }
}
