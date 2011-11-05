/*
 * Created on 17/set/2010
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
package org.sejda.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;

/**
 * @author Andrea Vacondio
 * 
 */
public class EncryptParametersTest {

    @Test
    public void testAdd() {
        EncryptParameters victim = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        victim.addPermission(PdfAccessPermission.ANNOTATION);
        assertEquals(1, victim.getPermissions().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableList() {
        EncryptParameters victim = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        victim.addPermission(PdfAccessPermission.ANNOTATION);
        victim.getPermissions().clear();
    }

    @Test
    public void testClear() {
        EncryptParameters victim = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        victim.addPermission(PdfAccessPermission.ANNOTATION);
        assertEquals(1, victim.getPermissions().size());
        victim.clearPermissions();
        assertEquals(0, victim.getPermissions().size());
    }

    @Test
    public void testEqual() {
        EncryptParameters eq1 = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        eq1.addPermission(PdfAccessPermission.COPY_AND_EXTRACT);
        EncryptParameters eq2 = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        eq2.addPermission(PdfAccessPermission.COPY_AND_EXTRACT);
        EncryptParameters eq3 = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        eq3.addPermission(PdfAccessPermission.COPY_AND_EXTRACT);
        EncryptParameters diff = new EncryptParameters(PdfEncryption.STANDARD_ENC_40);
        diff.addPermission(PdfAccessPermission.ASSEMBLE);
        diff.setVersion(PdfVersion.VERSION_1_2);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParameters() {
        EncryptParameters victim = new EncryptParameters(null);
        TaskOutput output = mock(TaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
