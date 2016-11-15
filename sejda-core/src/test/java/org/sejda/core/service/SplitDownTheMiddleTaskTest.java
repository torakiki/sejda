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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.split.SplitDownTheMiddleMode;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;

@Ignore
public abstract class SplitDownTheMiddleTaskTest extends BaseTaskTest<SplitDownTheMiddleParameters> {

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
    public void lastFirstRepagination() throws TaskException, IOException {
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
    public void lastFirstRepaginationUnevenPagePairs() throws TaskException, IOException {
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

    public void assertPageText(PDPage page, String text) {
        PDFTextStripperByArea textStripper;
        try {
            textStripper = new PDFTextStripperByArea();
            PDRectangle pageSize = page.getCropBox();
            Rectangle cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getWidth(), (int) pageSize.getHeight());
            if(page.getRotation() == 90 || page.getRotation() == 270) {
                cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getHeight(), (int) pageSize.getWidth());
            }
            textStripper.setSortByPosition(true);
            textStripper.addRegion("area1", cropBoxRectangle);
            textStripper.extractRegions(page);
            assertEquals(text, textStripper.getTextForRegion("area1").replaceAll("[^A-Za-z0-9]", ""));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void assertMediaBox(PDPage page, float width, float height) {
        assertEquals(page.getMediaBox().getWidth(), width, 0.01);
        assertEquals(page.getMediaBox().getHeight(), height, 0.01);
    }
}
