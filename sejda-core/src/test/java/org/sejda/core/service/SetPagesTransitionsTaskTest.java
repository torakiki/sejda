package org.sejda.core.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransition;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionDimension;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionMotion;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionStyle;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class SetPagesTransitionsTaskTest extends BaseTaskTest<SetPagesTransitionParameters> {
    private SetPagesTransitionParameters parameters;

    private void setUpParameters() throws IOException {
        parameters = new SetPagesTransitionParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.putTransition(1, PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_OUTWARD, 1, 5));
        parameters.setSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
    }

    @Test
    public void testExecute() throws IOException {
        setUpParameters();
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(4).forPdfOutput(d -> {
            PDTransition trans = d.getPage(0).getTransition();
            Assertions.assertEquals(PDTransitionStyle.Box.toString(), trans.getStyle());
            Assertions.assertEquals(PDTransitionMotion.O.toString(), trans.getMotion());
            assertNull(d.getPage(1).getTransition());
            assertNull(d.getPage(2).getTransition());
            assertNull(d.getPage(3).getTransition());
        });
    }

    @Test
    public void testExecuteDefault() throws IOException {
        parameters = new SetPagesTransitionParameters(
                PdfPageTransition.newInstance(PdfPageTransitionStyle.SPLIT_HORIZONTAL_INWARD, 1, 5));
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.putTransition(1, PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_OUTWARD, 1, 5));
        parameters.setSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(4).forEachPdfOutput(d -> {
            PDTransition trans = d.getPage(0).getTransition();
            Assertions.assertEquals(PDTransitionStyle.Box.toString(), trans.getStyle());
            Assertions.assertEquals(PDTransitionMotion.O.toString(), trans.getMotion());
            for (int i = 1; i < 4; i++) {
                PDTransition defTrans = d.getPage(i).getTransition();
                Assertions.assertEquals(PDTransitionStyle.Split.toString(), defTrans.getStyle());
                Assertions.assertEquals(PDTransitionMotion.I.toString(), defTrans.getMotion());
                Assertions.assertEquals(PDTransitionDimension.H.toString(), defTrans.getDimension());
            }
        });
    }

}
