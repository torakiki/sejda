/*
 * Copyright 2022 Sober Lemur S.r.l. and Sejda BV
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
import org.sejda.conversion.BasePageRangeAdapter.PageRangeAdapter;
import org.sejda.conversion.BasePageRangeAdapter.PageRangeWithAllAdapter;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.page.PageRange;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 3/11/12 4:08 PM
 *
 * @author: Edi Weissmann
 */
public class PageRangeAdapterTest {
    @Test
    public void testPositive() {
        assertThat(new PageRangeAdapter("1-3").getPageRange(), is(new PageRange(1, 3)));
        assertThat(new PageRangeAdapter("3").getPageRange(), is(new PageRange(3, 3)));
        assertThat(new PageRangeAdapter("7-").getPageRange(), is(new PageRange(7)));
        assertThat(new PageRangeAdapter("7 -8 ").getPageRange(), is(new PageRange(7, 8)));
        assertThat(new PageRangeWithAllAdapter("all").getPageRange(), is(new PageRange(1)));
        assertThat(new PageRangeWithAllAdapter("1-3").getPageRange(), is(new PageRange(1, 3)));
    }

    @Test
    public void testNegative() {
        assertThrows(ConversionException.class, () -> new PageRangeAdapter("1,3").getPageRange(),
                "Unparsable page range '1,3'");
        assertThrows(ConversionException.class, () -> new PageRangeAdapter("all").getPageRange(),
                "Unparsable page range 'all'");
        assertThrows(ConversionException.class, () -> new PageRangeAdapter("4-3").getPageRange(),
                "Invalid page range '4-3', ends before starting");
        assertThrows(ConversionException.class, () -> new PageRangeAdapter("1-3-4").getPageRange(),
                "Unparsable page range '1-3-4'");
    }

}
