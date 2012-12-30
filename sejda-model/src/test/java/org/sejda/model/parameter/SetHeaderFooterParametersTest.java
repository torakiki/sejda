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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.headerfooter.Numbering;
import org.sejda.model.pdf.headerfooter.NumberingStyle;

public class SetHeaderFooterParametersTest {
    private Numbering numbering = new Numbering(NumberingStyle.ARABIC, 100);
    private Numbering roman = new Numbering(NumberingStyle.ROMAN, 100);
    private SingleTaskOutput<?> output;

    @Before
    public void setUp() {
        output = mock(SingleTaskOutput.class);
    }

    @Test
    public void testEquals() {
        SetHeaderFooterParameters eq1 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters eq2 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters eq3 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters diff = new SetHeaderFooterParameters();
        eq1.setNumbering(numbering);
        eq2.setNumbering(numbering);
        eq3.setNumbering(numbering);
        diff.setNumbering(numbering);
        diff.setFont(StandardType1Font.CURIER_BOLD);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParameters() {
        SetHeaderFooterParameters victim = new SetHeaderFooterParameters();
        victim.setOutput(output);
        victim.setNumbering(numbering);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testFormatForLabelWithPrefix() {
        SetHeaderFooterParameters victim = new SetHeaderFooterParameters();
        victim.setNumbering(numbering);
        victim.setLabelPrefix("Prefix ");
        assertThat(victim.styledLabelFor(110), is("Prefix 110"));
    }

    @Test
    public void testFormatForEmptyNumberingStyle() {
        SetHeaderFooterParameters victim = new SetHeaderFooterParameters();
        victim.setLabelPrefix("Prefix");
        assertThat(victim.styledLabelFor(99), is("Prefix"));
    }

    @Test
    public void testFormatForLabelWithPrefixRomans() {
        SetHeaderFooterParameters victim = new SetHeaderFooterParameters();
        victim.setLabelPrefix("Prefix ");
        victim.setNumbering(roman);
        assertThat(victim.styledLabelFor(110), is("Prefix CX"));
    }
}
