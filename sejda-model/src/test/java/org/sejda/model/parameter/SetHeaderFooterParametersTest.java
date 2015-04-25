/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.numbering.BatesSequence;

public class SetHeaderFooterParametersTest {

    @Test
    public void testEquals() {
        BatesSequence bates = new BatesSequence();

        SetHeaderFooterParameters eq1 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters eq2 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters eq3 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters diff = new SetHeaderFooterParameters();
        eq1.setBatesSequence(bates);
        eq2.setBatesSequence(bates);
        eq3.setBatesSequence(bates);
        diff.setBatesSequence(bates);
        diff.setFont(StandardType1Font.CURIER_BOLD);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testValidation() {
        SetHeaderFooterParameters victim = new SetHeaderFooterParameters();
        victim.setPageCountStartFrom(-1);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
