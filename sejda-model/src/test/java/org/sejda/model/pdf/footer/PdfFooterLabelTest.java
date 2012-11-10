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
package org.sejda.model.pdf.footer;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PdfFooterLabelTest extends TestCase {

    public void testFormatForLabelWithoutPrefix() throws Exception {
        PdfFooterLabel label = PdfFooterLabel.newInstanceNoLabelPrefix(FooterNumberingStyle.ARABIC, 100);
        assertThat(label.formatFor(10), is("110"));
    }

    public void testFormatForLabelWithPrefix() throws Exception {
        PdfFooterLabel label = PdfFooterLabel.newInstanceWithLabelPrefixAndNumbering("Prefix ", FooterNumberingStyle.ARABIC, 100);
        assertThat(label.formatFor(10), is("Prefix 110"));
    }

    public void testFormatForEmptyNumberingStyle() throws Exception {
        PdfFooterLabel label = PdfFooterLabel.newInstanceTextOnly("Prefix");
        assertThat(label.formatFor(99), is("Prefix"));
    }
}
