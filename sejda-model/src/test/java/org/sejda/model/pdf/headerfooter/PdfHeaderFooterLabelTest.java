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
package org.sejda.model.pdf.headerfooter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sejda.model.pdf.headerfooter.NumberingStyle;
import org.sejda.model.pdf.headerfooter.PdfHeaderFooterLabel;

public class PdfHeaderFooterLabelTest {

    @Test
    public void testFormatForLabelWithoutPrefix() {
        PdfHeaderFooterLabel label = PdfHeaderFooterLabel.newInstanceNoLabelPrefix(NumberingStyle.ARABIC, 100);
        assertThat(label.formatFor(10), is("110"));
    }

    @Test
    public void testFormatForLabelWithPrefix() {
        PdfHeaderFooterLabel label = PdfHeaderFooterLabel.newInstanceWithLabelPrefixAndNumbering("Prefix ",
                NumberingStyle.ARABIC, 100);
        assertThat(label.formatFor(10), is("Prefix 110"));
    }

    @Test
    public void testFormatForEmptyNumberingStyle() {
        PdfHeaderFooterLabel label = PdfHeaderFooterLabel.newInstanceTextOnly("Prefix");
        assertThat(label.formatFor(99), is("Prefix"));
    }

    @Test
    public void testFormatForLabelWithPrefixRomans() {
        PdfHeaderFooterLabel label = PdfHeaderFooterLabel.newInstanceWithLabelPrefixAndNumbering("Prefix ",
                NumberingStyle.ROMAN, 100);
        assertThat(label.formatFor(10), is("Prefix CX"));
    }
}
