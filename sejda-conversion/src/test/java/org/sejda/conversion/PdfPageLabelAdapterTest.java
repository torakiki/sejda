/*
 * Copyright 2022 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.conversion;

import org.junit.jupiter.api.Test;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 6/16/12 3:17 PM
 *
 * @author: Edi Weissmann
 */
public class PdfPageLabelAdapterTest {

    @Test
    public void positives() {
        assertThat(new PdfPageLabelAdapter("22:arabic:1").getPageNumber(), is(22));
        assertThat(new PdfPageLabelAdapter("1:arabic:1").getPdfPageLabel().getNumberingStyle(),
                is(PdfLabelNumberingStyle.ARABIC));
        assertThat(new PdfPageLabelAdapter("1:empty:1").getPdfPageLabel().getNumberingStyle(),
                is(PdfLabelNumberingStyle.EMPTY));
        assertThat(new PdfPageLabelAdapter("1:lletter:1").getPdfPageLabel().getNumberingStyle(),
                is(PdfLabelNumberingStyle.LOWERCASE_LETTERS));
        assertThat(new PdfPageLabelAdapter("22:arabic:1:label").getPdfPageLabel().getLabelPrefix(), is("label"));
    }

    @Test
    public void negatives() {
        assertThrows(ConversionException.class, () -> new PdfPageLabelAdapter("22:arabic"));
    }
}
