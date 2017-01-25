/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 *
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.split.SplitDownTheMiddleMode;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationPopup;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public abstract class SplitDownTheMiddleTaskTest extends BaseTaskTest<SplitDownTheMiddleParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitDownTheMiddleTaskTest.class);

    private SplitDownTheMiddleParameters parameters;

    private void setUpParameters(String source) throws IOException {
        parameters = new SplitDownTheMiddleParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput(source));
        testContext.directoryOutputTo(parameters);
    }

    @Test
    public void splitLandscapeMode() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "L1L1");
            assertPageText(d.getPage(1), "R1R1");
            assertPageText(d.getPage(2), "L2L2");
            assertPageText(d.getPage(3), "R2R2");
        });

    }

    @Test
    public void excludePages() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample.pdf");
        parameters.addExcludedPage(1);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(3).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "L1L1R1R1");
            assertPageText(d.getPage(1), "L2L2");
            assertPageText(d.getPage(2), "R2R2");
        });
    }

    @Test
    public void userSpecifiedSplitMode() throws IOException {
        setUpParameters("pdf/alphabet.pdf");
        parameters.setMode(SplitDownTheMiddleMode.VERTICAL);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "ABCFGHKLMPQRUVWX");
            assertPageText(d.getPage(1), "DEIJNOSTYZ");
        });
    }

    @Test
    public void croppedPortrait() throws IOException {
        setUpParameters("pdf/cropped_alphabet.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "FGHKLMPQRUVWX");
            assertPageText(d.getPage(1), "IJNOSTYZ");
        });
    }

    @Test
    public void croppedRotatedLandscape() throws IOException {
        setUpParameters("pdf/cropped_rotated_alphabet.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "PQRSUVWXY");
            assertPageText(d.getPage(1), "FGHIKLMN");
        });
    }

    @Test
    public void outlineHandling() throws IOException {
        setUpParameters("pdf/test_outline.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(6).forPdfOutput(d -> {

            List<PDOutlineItem> outlineItems = iteratorToList(
                    d.getDocumentCatalog().getDocumentOutline().children().iterator());
            assertThat(outlineItems.size(), is(2));
        });
    }

    @Test
    public void excludedPages() throws IOException {
        setUpParameters("pdf/test_outline.pdf");
        parameters.addExcludedPage(1);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(5);
    }

    @Test
    public void annotationsHandling() throws IOException {
        setUpParameters("pdf/alphabet.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {

            logAnnotations(d, 0);
            logAnnotations(d, 1);

            assertEquals(2, d.getPage(0).getAnnotations().size());
            assertEquals(3, d.getPage(1).getAnnotations().size());

            PDAnnotationSquareCircle ab = getAnnotationsOf(d.getPage(0), PDAnnotationSquareCircle.class).get(0);
            assertThat(ab.getColor().getComponents(), is(new float[] { 0.9904157F, 0.5002688F, 0.0328658F }));
            assertThat(ab.getRectangle(), is(asRect(42.32959, 186.22998, 249.2938, 343.203)));

            PDAnnotationSquareCircle t = getAnnotationsOf(d.getPage(1), PDAnnotationSquareCircle.class).get(0);
            assertThat(t.getRectangle(), is(asRect(430.8547, 180.1956, 550.4685, 317.6999)));

            PDAnnotationPopup tNote = getAnnotationsOf(d.getPage(1), PDAnnotationPopup.class).get(0);
            assertThat(tNote.getRectangle(), is(asRect(322.0, 334.0, 450.0, 398.0)));
        });
    }

    @Test
    public void annotationsHandlingCropped() throws IOException {
        setUpParameters("pdf/cropped_alphabet.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {
            assertEquals(1, d.getPage(0).getAnnotations().size());
            assertEquals(3, d.getPage(1).getAnnotations().size());

            PDAnnotationSquareCircle annotation = getAnnotationsOf(d.getPage(1), PDAnnotationSquareCircle.class).get(0);
            assertThat(annotation.getColor().getComponents(), is(new float[] { 0.986246F, 0.007120788F, 0.02743419F }));
            assertThat(annotation.getRectangle(), is(asRect(105.275604, 261.5932, 212.5824, 363.9699)));
        });
    }

    public void logAnnotations(PDDocument doc, int page) {
        List<PDAnnotation> annotations = doc.getPage(page).getAnnotations();
        LOG.info("Page {} has {} annotations", page, annotations.size());
        for (PDAnnotation a : annotations) {
            LOG.info("{}", a);
        }
    }

    @Test
    public void annotationsHandlingRotated() throws IOException {
        setUpParameters("pdf/rotated_alphabet.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {

            logAnnotations(d, 0);
            logAnnotations(d, 1);

            assertEquals(3, d.getPage(0).getAnnotations().size());
            assertEquals(3, d.getPage(1).getAnnotations().size());

            PDAnnotationSquareCircle orange = getAnnotationsOf(d.getPage(0), PDAnnotationSquareCircle.class).get(0);
            assertThat(orange.getColor().getComponents(), is(new float[] { 0.9904157F, 0.5002688F, 0.0328658F }));
            assertThat(orange.getRectangle(), is(asRect(305.4067, 258.3301, 462.3797, 465.2944)));

            PDAnnotationSquareCircle greenSquare = getAnnotationsOf(d.getPage(1), PDAnnotationSquareCircle.class)
                    .stream().filter(a -> Arrays.equals(a.getColor().getComponents(),
                            new float[] { 0.5254902F, 0.8039221F, 0.3019604F }))
                    .findFirst().get();
            assertThat(greenSquare.getRectangle(), is(asRect(197.16162, 254.07599, 334.66577, 373.68982)));
        });
    }

    private PDRectangle asRect(double lowerLeftX, double lowerLeftY, double upperRightX, double upperRightY) {
        return new PDRectangle((float) lowerLeftX, (float) lowerLeftY, (float) upperRightX - (float) lowerLeftX,
                (float) upperRightY - (float) lowerLeftY);
    }

    @Ignore("Does not work yet :(")
    public void annotationsHandlingRotatedCropped() throws IOException {
        setUpParameters("pdf/cropped_rotated_alphabet.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {
            assertEquals(d.getPage(0).getAnnotations().size(), 0);
            assertEquals(d.getPage(1).getAnnotations().size(), 1);

            PDAnnotationSquareCircle annotation = getAnnotationsOf(d.getPage(1), PDAnnotationSquareCircle.class).get(0);
            assertThat(annotation.getRectangle(), is(new PDRectangle(684, 561, 149, 45)));
        });
    }

    @Test
    public void userSpecifiedSplitModeRotated() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample_rotated_90.pdf");
        parameters.setMode(SplitDownTheMiddleMode.VERTICAL);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "R1R1");
            assertPageText(d.getPage(1), "L1L1");
        });
    }

    @Test
    public void userSpecifiedRatio() throws IOException {
        setUpParameters("pdf/alphabet.pdf");
        parameters.setRatio(0.3);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "ABCDE");
            assertMediaBox(d.getPage(0), 612f, 182.76f);
            assertPageText(d.getPage(1), "FGHIJKLMNOPQRSTUVWXYZ");
            assertMediaBox(d.getPage(1), 612f, 609.23f);
        });
    }

    @Test
    public void splitLandscapeModeRotated90() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample_rotated_90.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertMediaBox(d.getPage(0), 842.f, 595.0f);
            assertPageText(d.getPage(0), "L1L1");
            assertPageText(d.getPage(1), "R1R1");
            assertPageText(d.getPage(2), "L2L2");
            assertPageText(d.getPage(3), "R2R2");
        });

    }

    @Test
    public void splitLandscapeModeRotated180() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample_rotated_180.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "R1R1");
            assertPageText(d.getPage(1), "L1L1");
            assertPageText(d.getPage(2), "R2R2");
            assertPageText(d.getPage(3), "L2L2");
        });

    }

    @Test
    public void splitLandscapeModeRotated270() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample_rotated_270.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "R1R1");
            assertPageText(d.getPage(1), "L1L1");
            assertPageText(d.getPage(2), "R2R2");
            assertPageText(d.getPage(3), "L2L2");
        });

    }

    @Test
    public void splitPortraitModeRotated90() throws IOException {
        setUpParameters("pdf/split_in_two_portrait_sample_rotated_90.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "R1R1");
            assertPageText(d.getPage(1), "L1L1");
            assertPageText(d.getPage(2), "R2R2");
            assertPageText(d.getPage(3), "L2L2");
        });

    }

    @Test
    public void splitPortraitModeRotated180() throws IOException {
        setUpParameters("pdf/split_in_two_portrait_sample_rotated_180.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "R1R1");
            assertPageText(d.getPage(1), "L1L1");
            assertPageText(d.getPage(2), "R2R2");
            assertPageText(d.getPage(3), "L2L2");
        });

    }

    @Test
    public void splitPortraitModeRotated270() throws IOException {
        setUpParameters("pdf/split_in_two_portrait_sample_rotated_270.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "L1L1");
            assertPageText(d.getPage(1), "R1R1");
            assertPageText(d.getPage(2), "L2L2");
            assertPageText(d.getPage(3), "R2R2");
        });

    }

    @Test
    public void splitPortraitMode() throws IOException {
        setUpParameters("pdf/split_in_two_portrait_sample.pdf");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "L1L1");
            assertPageText(d.getPage(1), "R1R1");
            assertPageText(d.getPage(2), "L2L2");
            assertPageText(d.getPage(3), "R2R2");
        });
    }

    @Test
    public void lastFirstRepagination() throws IOException {
        setUpParameters("pdf/split_in_two_last_first_repagination_sample.pdf");
        parameters.setRepagination(Repagination.LAST_FIRST);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(10).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "1");
            assertPageText(d.getPage(1), "2");
            assertPageText(d.getPage(2), "3");
            assertPageText(d.getPage(3), "4");
            assertPageText(d.getPage(4), "5");
            assertPageText(d.getPage(5), "6");
            assertPageText(d.getPage(6), "7");
            assertPageText(d.getPage(7), "8");
            assertPageText(d.getPage(8), "9");
            assertPageText(d.getPage(9), "10");
        });
    }

    @Test
    public void lastFirstRepaginationUnevenPagePairs() throws IOException {
        setUpParameters("pdf/split_in_two_last_first_repagination_uneven_sample.pdf");
        parameters.setRepagination(Repagination.LAST_FIRST);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(12).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "1");
            assertPageText(d.getPage(1), "2");
            assertPageText(d.getPage(2), "3");
            assertPageText(d.getPage(3), "4");
            assertPageText(d.getPage(4), "5");
            assertPageText(d.getPage(5), "6");
            assertPageText(d.getPage(6), "7");
            assertPageText(d.getPage(7), "8");
            assertPageText(d.getPage(8), "9");
            assertPageText(d.getPage(9), "10");
            assertPageText(d.getPage(10), "11");
            assertPageText(d.getPage(11), "12");
        });
    }

    @Test
    public void rightToLeftLandscapeMode() throws IOException {
        setUpParameters("pdf/split_in_two_landscape_sample.pdf");
        parameters.setRightToLeft(true);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(4).forPdfOutput(d -> {
            assertPageText(d.getPage(0), "R1R1");
            assertPageText(d.getPage(1), "L1L1");
            assertPageText(d.getPage(2), "R2R2");
            assertPageText(d.getPage(3), "L2L2");
        });

    }
}
