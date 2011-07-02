package org.sejda.core.manipulation.model.pdf.transition;

import java.security.InvalidParameterException;

import org.junit.Assert;
import org.junit.Test;
import org.sejda.core.TestUtils;

/**
 * Test unit for PdfPageTransition
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfPageTransitionTest {

    @Test(expected = InvalidParameterException.class)
    public void testNullStyle() {
        PdfPageTransition.newInstance(null, 1, 1, 1);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNegativePageNumber() {
        PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, -1, 1, 1);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNoTransitionDuration() {
        PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 0, 1);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNoDisplayDuration() {
        PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 1, 0);
    }

    @Test
    public void testEveryPage() {
        PdfPageTransition victim = PdfPageTransition.newInstanceEveryPage(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1,
                1);
        Assert.assertTrue(victim.isEveryPage());
    }

    @Test
    public void testEqualsAndHashCodes() {
        PdfPageTransition victim1 = PdfPageTransition.newInstanceEveryPage(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1,
                1);
        PdfPageTransition victim2 = PdfPageTransition.newInstanceEveryPage(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1,
                1);
        PdfPageTransition victim3 = PdfPageTransition.newInstanceEveryPage(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1,
                1);
        PdfPageTransition victim4 = PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 1, 1);
        TestUtils.testEqualsAndHashCodes(victim1, victim2, victim3, victim4);
    }
}
