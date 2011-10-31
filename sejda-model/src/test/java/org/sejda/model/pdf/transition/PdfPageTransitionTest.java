package org.sejda.model.pdf.transition;

import java.security.InvalidParameterException;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * Test unit for PdfPageTransition
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfPageTransitionTest {

    @Test(expected = InvalidParameterException.class)
    public void testNullStyle() {
        PdfPageTransition.newInstance(null, 1, 1);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNoTransitionDuration() {
        PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 0, 1);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNoDisplayDuration() {
        PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 0);
    }

    @Test
    public void testEqualsAndHashCodes() {
        PdfPageTransition victim1 = PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 1);
        PdfPageTransition victim2 = PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 1);
        PdfPageTransition victim3 = PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 1);
        PdfPageTransition victim4 = PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 2);
        TestUtils.testEqualsAndHashCodes(victim1, victim2, victim3, victim4);
    }
}
