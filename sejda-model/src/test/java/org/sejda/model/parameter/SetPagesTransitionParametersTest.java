package org.sejda.model.parameter;

import org.junit.jupiter.api.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;
import org.sejda.model.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SetPagesTransitionParametersTest {


    @Test
    public void testEquals() {
        SetPagesTransitionParameters victim1 = new SetPagesTransitionParameters();
        SetPagesTransitionParameters victim2 = new SetPagesTransitionParameters();
        SetPagesTransitionParameters victim3 = new SetPagesTransitionParameters();
        SetPagesTransitionParameters victim4 = new SetPagesTransitionParameters(PdfPageTransition.newInstance(
                PdfPageTransitionStyle.DISSOLVE, 1, 5));
        TestUtils.testEqualsAndHashCodes(victim1, victim2, victim3, victim4);
    }

    @Test
    public void testPutTransition() {
        SetPagesTransitionParameters victim = new SetPagesTransitionParameters();
        PdfPageTransition firstTransition = PdfPageTransition.newInstance(PdfPageTransitionStyle.DISSOLVE, 1, 5);
        victim.putTransition(3, firstTransition);
        assertEquals(1, victim.getTransitions().size());
        PdfPageTransition secondTransition = PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_INWARD, 1, 2);
        PdfPageTransition result = victim.putTransition(3, secondTransition);
        assertEquals(firstTransition, result);
        assertEquals(1, victim.getTransitions().size());
    }

    @Test
    public void testInvalidParameters() throws IOException {
        SetPagesTransitionParameters victim = new SetPagesTransitionParameters(null);
        SingleTaskOutput output = new FileTaskOutput(Files.createTempFile(null, ".pdf").toFile());
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
