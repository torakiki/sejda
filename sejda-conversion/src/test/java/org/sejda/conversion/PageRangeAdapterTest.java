package org.sejda.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.sejda.conversion.BasePageRangeAdapter.PageRangeAdapter;
import org.sejda.conversion.BasePageRangeAdapter.PageRangeWithAllAdapter;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.page.PageRange;

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
        failsWith("1,3", "Unparsable page range '1,3'");
        failsWith("all", "Unparsable page range 'all'");
        failsWith("4-3", "Invalid page range '4-3', ends before starting");
        failsWith("1-3-4", "Unparsable page range '1-3-4'");
    }

    private void failsWith(String input, String expectedMsg) {
        try {
            new PageRangeAdapter(input).getPageRange();
            fail("Expected conversion exception: " + expectedMsg);
        } catch (ConversionException e) {
            assertThat(e.getMessage(), containsString(expectedMsg));
        }
    }
}
