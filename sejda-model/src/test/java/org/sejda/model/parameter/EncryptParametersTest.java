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
package org.sejda.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.SingleOrMultipleTaskOutput;
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
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
