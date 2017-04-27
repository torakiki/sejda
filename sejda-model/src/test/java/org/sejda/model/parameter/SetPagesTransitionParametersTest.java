package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

public class SetPagesTransitionParametersTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        Assert.assertEquals(1, victim.getTransitions().size());
        PdfPageTransition secondTransition = PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_INWARD, 1, 2);
        PdfPageTransition result = victim.putTransition(3, secondTransition);
        Assert.assertEquals(firstTransition, result);
        Assert.assertEquals(1, victim.getTransitions().size());
    }

    @Test
    public void testInvalidParameters() throws IOException {
        SetPagesTransitionParameters victim = new SetPagesTransitionParameters(null);
        SingleTaskOutput output = new FileTaskOutput(folder.newFile());
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
