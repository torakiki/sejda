package org.sejda.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.sejda.model.rotation.Rotation.DEGREES_0;
import static org.sejda.model.rotation.Rotation.DEGREES_180;
import static org.sejda.model.rotation.Rotation.DEGREES_270;
import static org.sejda.model.rotation.Rotation.DEGREES_90;

import org.junit.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.rotation.PageRotation;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.rotation.RotationType;

/**
 * Created on 6/7/12 10:02 PM
 * 
 * @author: Edi Weissmann
 */
public class PageRotationAdapterTest {

    @Test
    public void testGetPageRotation() {

        assertSinglePageRotation("2:90", 2, DEGREES_90);
        assertSinglePageRotation("1:0", 1, DEGREES_0);
        assertSinglePageRotation("1222:270", 1222, DEGREES_270);
        assertSinglePageRotation("1222:180", 1222, DEGREES_180);

        assertMultiplePagesRotation("all:90", RotationType.ALL_PAGES);
        assertMultiplePagesRotation("even:90", RotationType.EVEN_PAGES);
        assertMultiplePagesRotation("odd:90", RotationType.ODD_PAGES);

        assertConversionException("bla:90", "Unknown page definition: 'bla'");
        assertConversionException("2:hmm", "Unknown rotation: 'hmm'");
        assertConversionException("bla", "Invalid input");
    }

    private void assertSinglePageRotation(String input, int expectedPage, Rotation expectedRotation) {
        PageRotation pageRotation = new PageRotationAdapter(input).getPageRotation();

        assertThat(pageRotation.getPageNumber(), is(expectedPage));
        assertThat(pageRotation.getRotationType(), is(RotationType.SINGLE_PAGE));
        assertThat(pageRotation.getRotation(), is(expectedRotation));
    }

    private void assertMultiplePagesRotation(String input, RotationType expectedRotationType) {
        PageRotation pageRotation = new PageRotationAdapter(input).getPageRotation();

        assertThat(pageRotation.getRotationType(), is(expectedRotationType));
    }

    private void assertConversionException(String input, String expectedMessage) {
        try {
            new PageRotationAdapter(input).getPageRotation();
            fail("expected a conversion exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString(expectedMessage));
        }
    }

    @Test
    public void singlePage() {
        try {
            new PageRotationAdapter("abc:90").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown page definition: 'abc'"));
        }
    }

    @Test
    public void singlePage_nonNumericPage() {
        PageRotation expected = PageRotation.createSinglePageRotation(77, Rotation.DEGREES_270);
        assertEquals(expected, new PageRotationAdapter("77:270").getPageRotation());
    }

    @Test
    public void multiplePages() {
        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_0, RotationType.ALL_PAGES),
                new PageRotationAdapter("all:0").getPageRotation());

        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_180, RotationType.EVEN_PAGES),
                new PageRotationAdapter("even:180").getPageRotation());

        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_90, RotationType.ODD_PAGES),
                new PageRotationAdapter("odd:90").getPageRotation());
    }

    @Test
    public void negative_noSeparator() {
        try {
            new PageRotationAdapter("all0").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(),
                    containsString("Invalid input: 'all0'. Expected format: 'pageDefinition:rotation'"));
        }
    }

    @Test
    public void negative_unrecognizedPages() {
        try {
            new PageRotationAdapter("some:0").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown page definition: 'some'"));
        }
    }

    @Test
    public void negative_unrecognizedRotation() {
        try {
            new PageRotationAdapter("odd:99").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown rotation: '99'"));
        }
    }

    @Test
    public void negative_nonNumbericRotation() {
        try {
            new PageRotationAdapter("odd:99abc").getPageRotation();
            fail();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("Unknown rotation: '99abc'"));
        }
    }
}
