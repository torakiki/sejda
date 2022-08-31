package org.sejda.model.pdf.transition;

import org.junit.jupiter.api.Test;
import org.sejda.tests.TestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test unit for PdfPageTransition
 *
 * @author Andrea Vacondio
 */
public class PdfPageTransitionTest {

    @Test
    public void testNullStyle() {
        assertThrows(IllegalArgumentException.class, () -> PdfPageTransition.newInstance(null, 1, 1));
    }

    @Test
    public void testNoTransitionDuration() {
        assertThrows(IllegalArgumentException.class,
                () -> PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 0, 1));
    }

    @Test
    public void testNoDisplayDuration() {
        assertThrows(IllegalArgumentException.class,
                () -> PdfPageTransition.newInstance(PdfPageTransitionStyle.BLINDS_HORIZONTAL, 1, 0));
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
