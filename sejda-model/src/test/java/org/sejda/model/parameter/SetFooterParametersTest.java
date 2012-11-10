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

import junit.framework.TestCase;
import org.sejda.model.pdf.footer.FooterNumberingStyle;
import org.sejda.model.pdf.footer.PdfFooterLabel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SetFooterParametersTest extends TestCase {
    PdfFooterLabel label1 = new PdfFooterLabel("Prefix1 ", FooterNumberingStyle.ARABIC, 100);
    PdfFooterLabel label2 = new PdfFooterLabel("Prefix2 ", FooterNumberingStyle.EMPTY, -100);

    public SetFooterParameters parameters() {
        SetFooterParameters params = new SetFooterParameters();
        params.putLabel(8, label1);
        params.putLabel(16, label2);
        return params;
    }

    public void testPutLabel() throws Exception {
        SetFooterParameters params = new SetFooterParameters();
        params.putLabel(8, label1);

        assertThat(params.putLabel(8, label2), is(label1));
    }

    public void testFormatLabelForUnlabeledPage() throws Exception {
        assertThat(parameters().formatLabelFor(1), is(nullValue()));
    }

    public void testFormatLabel() {
        assertThat(parameters().formatLabelFor(8), is("Prefix1 100"));
        assertThat(parameters().formatLabelFor(9), is("Prefix1 101"));
        assertThat(parameters().formatLabelFor(15), is("Prefix1 107"));
        assertThat(parameters().formatLabelFor(16), is("Prefix2 "));
        assertThat(parameters().formatLabelFor(17), is("Prefix2 "));
        assertThat(parameters().formatLabelFor(179663782), is("Prefix2 "));
    }
}
