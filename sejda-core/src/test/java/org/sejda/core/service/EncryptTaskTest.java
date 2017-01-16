/*
 * Created on 17/set/2010
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
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;

/**
 * Test unit for the encrypt task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class EncryptTaskTest extends BaseTaskTest<EncryptParameters> {

    private EncryptParameters parameters;

    private void setUpParameters(PdfEncryption encryption) {
        parameters = new EncryptParameters(encryption);
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    public void arc128WithOwner() throws IOException {
        setUpParameters(PdfEncryption.STANDARD_ENC_128);
        parameters.setOwnerPassword("test");
        parameters.addPermission(PdfAccessPermission.COPY_AND_EXTRACT);
        parameters.addPermission(PdfAccessPermission.FILL_FORMS);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).forEachPdfOutput(d -> {
            assertTrue(d.isEncrypted());
            assertTrue(d.getCurrentAccessPermission().canExtractContent());
            assertTrue(d.getCurrentAccessPermission().canFillInForm());

        });
    }

    @Test
    public void aes256WithOwner() throws IOException {
        setUpParameters(PdfEncryption.AES_ENC_256);
        parameters.setVersion(PdfVersion.VERSION_1_7);
        parameters.setOwnerPassword("Chuck");
        parameters.setUserPassword("Norris");
        parameters.addPermission(PdfAccessPermission.COPY_AND_EXTRACT);
        parameters.addPermission(PdfAccessPermission.FILL_FORMS);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted("Norris");
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_7).forEachPdfOutput(d -> {
            assertTrue(d.isEncrypted());
            assertTrue(d.getCurrentAccessPermission().canExtractContent());
            assertTrue(d.getCurrentAccessPermission().canFillInForm());

        });
    }

    @Test
    public void enablingPrintDegraded() throws IOException {
        setUpParameters(PdfEncryption.STANDARD_ENC_128);
        parameters.setOwnerPassword("test");
        parameters.addPermission(PdfAccessPermission.DEGRADATED_PRINT);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(d -> {
            assertTrue(d.isEncrypted());
            assertFalse(d.getCurrentAccessPermission().canPrint());
            assertTrue(d.getCurrentAccessPermission().canPrintDegraded());
        });
    }

    @Test
    public void enablingPrint() throws IOException {
        setUpParameters(PdfEncryption.STANDARD_ENC_128);
        parameters.setOwnerPassword("test");
        parameters.addPermission(PdfAccessPermission.PRINT);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(d -> {
            assertTrue(d.isEncrypted());
            assertTrue(d.getCurrentAccessPermission().canPrint());
            assertFalse(d.getCurrentAccessPermission().canPrintDegraded());
        });
    }

    @Test
    public void defaultPermissions() throws IOException {
        setUpParameters(PdfEncryption.STANDARD_ENC_128);
        assertEquals(parameters.getPermissions().size(), 0);

        parameters.setOwnerPassword("test");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(d -> {
            assertTrue(d.isEncrypted());
            assertFalse(d.getCurrentAccessPermission().canAssembleDocument());
            assertFalse(d.getCurrentAccessPermission().canExtractContent());
            assertTrue(d.getCurrentAccessPermission().canExtractForAccessibility());
            assertFalse(d.getCurrentAccessPermission().canFillInForm());
            assertFalse(d.getCurrentAccessPermission().canModify());
            assertFalse(d.getCurrentAccessPermission().canModifyAnnotations());
            assertFalse(d.getCurrentAccessPermission().canPrint());
            assertFalse(d.getCurrentAccessPermission().canPrintDegraded());
        });
    }
}
