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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.footer.FooterNumberingStyle;
import org.sejda.model.pdf.footer.PdfFooterLabel;

public class SetFooterParametersTest {
    PdfFooterLabel label1 = PdfFooterLabel.newInstanceWithLabelPrefixAndNumbering("Prefix1 ",
            FooterNumberingStyle.ARABIC, 100);
    PdfFooterLabel label2 = PdfFooterLabel.newInstanceTextOnly("Prefix2 ");

    public SetFooterParameters parameters() {
        SetFooterParameters params = new SetFooterParameters();
        params.putLabel(8, label1);
        params.putLabel(16, label2);
        return params;
    }

    @Test
    public void testPutLabel() {
        SetFooterParameters params = new SetFooterParameters();
        params.putLabel(8, label1);

        assertThat(params.putLabel(8, label2), is(label1));
    }

    @Test
    public void testFormatLabelForUnlabeledPage() {
        assertThat(parameters().formatLabelFor(1), is(nullValue()));
    }

    @Test
    public void testEquals() {
        SetFooterParameters eq1 = new SetFooterParameters();
        SetFooterParameters eq2 = new SetFooterParameters();
        SetFooterParameters eq3 = new SetFooterParameters();
        SetFooterParameters diff = new SetFooterParameters();
        eq1.putLabel(8, label1);
        eq2.putLabel(8, label1);
        eq3.putLabel(8, label1);
        diff.putLabel(8, label1);
        diff.setFont(StandardType1Font.CURIER_BOLD);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testFormatLabel() {
        assertThat(parameters().formatLabelFor(8), is("Prefix1 100"));
        assertThat(parameters().formatLabelFor(9), is("Prefix1 101"));
        assertThat(parameters().formatLabelFor(15), is("Prefix1 107"));
        assertThat(parameters().formatLabelFor(16), is("Prefix2"));
        assertThat(parameters().formatLabelFor(17), is("Prefix2"));
        assertThat(parameters().formatLabelFor(179663782), is("Prefix2"));
    }
}
