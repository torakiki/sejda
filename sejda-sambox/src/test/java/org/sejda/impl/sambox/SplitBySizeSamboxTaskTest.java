/*
 * Created on 31/ago/2015
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Test;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.CompressionPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.sejda.sambox.pdmodel.graphics.state.PDSoftMask;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.tests.TestUtils.customInput;
import static org.sejda.tests.TestUtils.mediumInput;
import static org.sejda.tests.TestUtils.regularInput;

/**
 * @author Andrea Vacondio
 */
public class SplitBySizeSamboxTaskTest extends BaseTaskTest<SplitBySizeParameters> {

    private SplitBySizeParameters parameters;

    @Test
    public void testExecute() throws IOException {
        parameters = new SplitBySizeParameters(100000);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(mediumInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
    }

    @Test
    public void batchMode() throws IOException {
        parameters = new SplitBySizeParameters(100000);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(mediumInput());
        parameters.addSource(regularInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]-[BASENAME]");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(5);
        testContext.assertOutputContainsFilenames("1-medium-test-file.pdf", "2-medium-test-file.pdf",
                "3-medium-test-file.pdf", "4-medium-test-file.pdf", "5-test-file.pdf");
    }

    @Test
    public void testExecuteOptimized() throws IOException {
        parameters = new SplitBySizeParameters(60000);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput("pdf/shared_resource_dic_with_2_imgs.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forEachRawOutput(p -> {
            try {
                assertTrue(Files.size(p) < 100000);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testExecuteNoOptimized() throws IOException {
        parameters = new SplitBySizeParameters(60000);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setOptimizationPolicy(OptimizationPolicy.NO);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput("pdf/shared_resource_dic_with_2_imgs.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forEachRawOutput(p -> {
            try {
                assertTrue(Files.size(p) > 100000);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void specificResultFilenames() throws IOException {
        parameters = new SplitBySizeParameters(100000);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(regularInput());
        parameters.addSource(mediumInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]-[BASENAME]");
        testContext.directoryOutputTo(parameters);
        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        parameters.addSpecificResultFilename("some/*?Invalid<chars");

        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(5)
                .assertOutputContainsFilenames("one.pdf", "two.pdf", "someInvalidchars.pdf", "4-medium-test-file.pdf",
                        "5-medium-test-file.pdf");
    }

    @Test
    // form resources have the same Font, ExtGState and XObject dictionaries of the pages
    public void optimizedFormSharingNameDictionariesWithPages() throws IOException {
        parameters = new SplitBySizeParameters(1_000_000);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_name_dics_page_and_form.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).forEachPdfOutput(d -> {
            try {
                PDResources page1Res = d.getPage(0).getResources();
                assertNotNull(page1Res.getFont(COSName.getPDFName("F1")));
                assertNotNull(page1Res.getExtGState(COSName.getPDFName("GS1")));
                assertTrue(page1Res.isImageXObject(COSName.getPDFName("Im1")));
                assertNull(page1Res.getFont(COSName.getPDFName("FU")));
                assertNull(page1Res.getExtGState(COSName.getPDFName("GSU")));
                assertFalse(page1Res.isImageXObject(COSName.getPDFName("ImU")));
                assertTrue(page1Res.isFormXObject(COSName.getPDFName("Fm")));
                PDResources formRes = ((PDFormXObject) page1Res.getXObject(COSName.getPDFName("Fm"))).getResources();
                assertNotNull(formRes.getFont(COSName.getPDFName("FN")));
                assertNotNull(formRes.getExtGState(COSName.getPDFName("GSN")));
                assertTrue(formRes.isImageXObject(COSName.getPDFName("ImN")));
                PDResources page2Res = d.getPage(1).getResources();
                assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
                assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
                assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
                assertNull(page2Res.getFont(COSName.getPDFName("FU")));
                assertNull(page2Res.getExtGState(COSName.getPDFName("GSU")));
                assertFalse(page2Res.isImageXObject(COSName.getPDFName("ImU")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    // form resources are the same resource dictionary of the pages
    public void optimizedFormSharingPageResources() throws IOException {
        parameters = new SplitBySizeParameters(1_000_000);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_res_dic_page_and_form.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).forEachPdfOutput(d -> {
            try {
                PDResources page1Res = d.getPage(0).getResources();
                assertNotNull(page1Res.getFont(COSName.getPDFName("F1")));
                assertNotNull(page1Res.getExtGState(COSName.getPDFName("GS1")));
                assertTrue(page1Res.isImageXObject(COSName.getPDFName("Im1")));
                assertNull(page1Res.getFont(COSName.getPDFName("FU")));
                assertNull(page1Res.getExtGState(COSName.getPDFName("GSU")));
                assertFalse(page1Res.isImageXObject(COSName.getPDFName("ImU")));
                assertTrue(page1Res.isFormXObject(COSName.getPDFName("Fm")));
                PDResources formRes = ((PDFormXObject) page1Res.getXObject(COSName.getPDFName("Fm"))).getResources();
                assertNotNull(formRes.getFont(COSName.getPDFName("FN")));
                assertNotNull(formRes.getExtGState(COSName.getPDFName("GSN")));
                assertTrue(formRes.isImageXObject(COSName.getPDFName("ImN")));
                PDResources page2Res = d.getPage(1).getResources();
                assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
                assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
                assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
                assertNull(page2Res.getFont(COSName.getPDFName("FU")));
                assertNull(page2Res.getExtGState(COSName.getPDFName("GSU")));
                assertFalse(page2Res.isImageXObject(COSName.getPDFName("ImU")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    // soft mask group resources have the same Font, ExtGState and XObject dictionaries of the pages
    public void optimizedSoftMaskSharingNameDictionariesWithPages() throws IOException {
        parameters = new SplitBySizeParameters(1_000_000);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_name_dics_page_and_softmask.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).forEachPdfOutput(d -> {
            try {
                PDResources page1Res = d.getPage(0).getResources();
                assertNotNull(page1Res.getFont(COSName.getPDFName("F1")));
                assertNotNull(page1Res.getExtGState(COSName.getPDFName("GS1")));
                assertTrue(page1Res.isImageXObject(COSName.getPDFName("Im1")));
                assertNull(page1Res.getFont(COSName.getPDFName("FU")));
                assertNull(page1Res.getExtGState(COSName.getPDFName("GSU")));
                assertFalse(page1Res.isImageXObject(COSName.getPDFName("ImU")));
                PDExtendedGraphicsState masked = page1Res.getExtGState(COSName.getPDFName("GSM"));
                assertNotNull(masked);
                PDSoftMask softMask = masked.getSoftMask();
                assertNotNull(softMask);
                PDResources maskRes = softMask.getGroup().getResources();
                assertNotNull(maskRes.getFont(COSName.getPDFName("FN")));
                assertNotNull(maskRes.getExtGState(COSName.getPDFName("GSN")));
                assertTrue(maskRes.isImageXObject(COSName.getPDFName("ImN")));
                PDResources page2Res = d.getPage(1).getResources();
                assertNotNull(page2Res.getFont(COSName.getPDFName("F2")));
                assertNotNull(page2Res.getExtGState(COSName.getPDFName("GS2")));
                assertTrue(page2Res.isImageXObject(COSName.getPDFName("Im2")));
                assertNull(page2Res.getFont(COSName.getPDFName("FU")));
                assertNull(page2Res.getExtGState(COSName.getPDFName("GSU")));
                assertFalse(page2Res.isImageXObject(COSName.getPDFName("ImU")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    // form FmA on page 1 and form FmB on page 2 have the same resource dictionary
    public void optimizedFormsSharingResources() throws IOException {
        parameters = new SplitBySizeParameters(1_000_000);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_res_dic_between_forms.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).forEachPdfOutput(d -> {
            try {
                PDResources page1Res = d.getPage(0).getResources();
                assertTrue(page1Res.isFormXObject(COSName.getPDFName("FmA")));
                PDResources formARes = ((PDFormXObject) page1Res.getXObject(COSName.getPDFName("FmA"))).getResources();
                assertNotNull(formARes.getFont(COSName.getPDFName("F1")));
                assertNotNull(formARes.getExtGState(COSName.getPDFName("GS1")));
                assertTrue(formARes.isImageXObject(COSName.getPDFName("Im1")));
                PDResources page2Res = d.getPage(1).getResources();
                assertTrue(page2Res.isFormXObject(COSName.getPDFName("FmB")));
                PDResources formBRes = ((PDFormXObject) page2Res.getXObject(COSName.getPDFName("FmB"))).getResources();
                assertNotNull(formBRes.getFont(COSName.getPDFName("F2")));
                assertNotNull(formBRes.getExtGState(COSName.getPDFName("GS2")));
                assertTrue(formBRes.isImageXObject(COSName.getPDFName("Im2")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    // form FmA on page 1 and form FmB on page 2 have the same resource dictionary and go in different files. Page 2 is predicted before the
    // first file is written, so FmB must not keep the resource dictionary pruned for the first file
    public void optimizedFormsSharingResourcesInDifferentFiles() throws IOException {
        parameters = new SplitBySizeParameters(1);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_res_dic_between_forms.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]-file");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forPdfOutput("1-file.pdf", d -> {
            try {
                PDResources pageRes = d.getPage(0).getResources();
                assertTrue(pageRes.isFormXObject(COSName.getPDFName("FmA")));
                PDResources formARes = ((PDFormXObject) pageRes.getXObject(COSName.getPDFName("FmA"))).getResources();
                assertNotNull(formARes.getFont(COSName.getPDFName("F1")));
                assertNotNull(formARes.getExtGState(COSName.getPDFName("GS1")));
                assertTrue(formARes.isImageXObject(COSName.getPDFName("Im1")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }).forPdfOutput("2-file.pdf", d -> {
            try {
                PDResources pageRes = d.getPage(0).getResources();
                assertTrue(pageRes.isFormXObject(COSName.getPDFName("FmB")));
                PDResources formBRes = ((PDFormXObject) pageRes.getXObject(COSName.getPDFName("FmB"))).getResources();
                assertNotNull(formBRes.getFont(COSName.getPDFName("F2")));
                assertNotNull(formBRes.getExtGState(COSName.getPDFName("GS2")));
                assertTrue(formBRes.isImageXObject(COSName.getPDFName("Im2")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    // form resources have the same Font, ExtGState and XObject dictionaries of the pages and the form is drawn on page 3. Splitting at 1 byte
    // generates single page files so file 2 is predicted with page 3 and file 4 comes after the form
    public void optimizedFormSharingNameDictionariesWithPagesOnPage3() throws IOException {
        parameters = new SplitBySizeParameters(1);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_name_dics_page_and_form_on_page_3.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]-file");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).forEachPdfOutput(d -> {
            try {
                PDResources pageRes = d.getPage(0).getResources();
                assertNotNull(pageRes.getFont(COSName.getPDFName("F1")));
                assertNotNull(pageRes.getExtGState(COSName.getPDFName("GS1")));
                assertTrue(pageRes.isImageXObject(COSName.getPDFName("Im1")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }).forPdfOutput("3-file.pdf",
                d -> assertTrue(d.getPage(0).getResources().isFormXObject(COSName.getPDFName("Fm"))));
    }

    @Test
    // pages have the same resource dictionary, containing one 10KB image for each page and form Fm. Fm is drawn on every page and its resource
    // dictionary is the one of the pages. 3 pages fit in 35KB as long as the size prediction doesn't count the images of the other pages through Fm
    public void optimizedFormSharingPageResourcesOnEveryPage() throws IOException {
        parameters = new SplitBySizeParameters(35_000);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_res_dic_page_and_form_every_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]-file");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertPages("1-file.pdf", 3).assertPages("2-file.pdf", 3).assertPages("3-file.pdf", 3)
                .assertPages("4-file.pdf", 1).forEachRawOutput(p -> {
                    try {
                        assertTrue(Files.size(p) < 35_000);
                    } catch (IOException e) {
                        fail(e.getMessage());
                    }
                }).forEachPdfOutput(d -> d.getPages()
                        .forEach(p -> assertTrue(p.getResources().isFormXObject(COSName.getPDFName("Fm")))))
                .forPdfOutput("1-file.pdf", d -> {
                    assertTrue(d.getPage(0).getResources().isImageXObject(COSName.getPDFName("Im0")));
                    assertTrue(d.getPage(1).getResources().isImageXObject(COSName.getPDFName("Im1")));
                    assertTrue(d.getPage(2).getResources().isImageXObject(COSName.getPDFName("Im2")));
                    d.getPages().forEach(p -> {
                        try {
                            assertFalse(p.getResources().isImageXObject(COSName.getPDFName("Im3")));
                            PDResources formRes = ((PDFormXObject) p.getResources().getXObject(COSName.getPDFName("Fm"))).getResources();
                            assertFalse(formRes.isImageXObject(COSName.getPDFName("Im3")));
                        } catch (IOException e) {
                            fail(e.getMessage());
                        }
                    });
                }).forPdfOutput("4-file.pdf",
                        d -> assertTrue(d.getPage(0).getResources().isImageXObject(COSName.getPDFName("Im9"))));
    }

    @Test
    // same as the previous one, but Fm draws form Fm2 and both forms have the resource dictionary of the pages. The prediction must prune the
    // resources of Fm and Fm2 as one dictionary, the way the extraction does, or it counts all the images through Fm2
    public void optimizedNestedFormsSharingPageResourcesOnEveryPage() throws IOException {
        parameters = new SplitBySizeParameters(35_000);
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addSource(customInput("pdf/shared_res_dic_page_and_nested_forms_every_page.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setOutputPrefix("[FILENUMBER]-file");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertPages("1-file.pdf", 3).assertPages("2-file.pdf", 3).assertPages("3-file.pdf", 3)
                .assertPages("4-file.pdf", 1).forEachRawOutput(p -> {
                    try {
                        assertTrue(Files.size(p) < 35_000);
                    } catch (IOException e) {
                        fail(e.getMessage());
                    }
                }).forEachPdfOutput(d -> d.getPages().forEach(p -> {
                    try {
                        assertTrue(p.getResources().isFormXObject(COSName.getPDFName("Fm")));
                        PDResources formRes = ((PDFormXObject) p.getResources().getXObject(COSName.getPDFName("Fm"))).getResources();
                        assertTrue(formRes.isFormXObject(COSName.getPDFName("Fm2")));
                    } catch (IOException e) {
                        fail(e.getMessage());
                    }
                })).forPdfOutput("1-file.pdf", d -> {
                    assertTrue(d.getPage(0).getResources().isImageXObject(COSName.getPDFName("Im0")));
                    assertTrue(d.getPage(1).getResources().isImageXObject(COSName.getPDFName("Im1")));
                    assertTrue(d.getPage(2).getResources().isImageXObject(COSName.getPDFName("Im2")));
                }).forPdfOutput("4-file.pdf",
                        d -> assertTrue(d.getPage(0).getResources().isImageXObject(COSName.getPDFName("Im9"))));
    }

    @Override
    public Task<SplitBySizeParameters> getTask() {
        return new SplitBySizeTask();
    }

}