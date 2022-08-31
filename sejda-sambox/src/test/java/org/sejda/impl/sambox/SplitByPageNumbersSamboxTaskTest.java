/*
 * Created on 03/08/2015
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 *
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitByPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SplitByPageNumbersSamboxTaskTest extends BaseTaskTest<SplitByPagesParameters> {
    private SplitByPagesParameters parameters;

    private void setUpParameters() throws IOException {
        parameters = new SplitByPagesParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
    }

    @Test
    public void burst() throws IOException {
        setUpParameters();
        parameters.addSource(shortInput());
        doTestBurst();
    }

    @Test
    public void burstOptimizeImages() throws IOException {
        setUpParameters();
        parameters.addSource(customInput("/pdf/shared_resource_dic_w_images.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.addPage(1);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void burstOptimizeFonts() throws IOException {
        setUpParameters();
        parameters.addSource(customInput("/pdf/shared_fonts.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.addPage(1);
        parameters.addPage(2);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
    }

    @Test
    public void burstEnc() throws IOException {
        setUpParameters();
        parameters.addSource(stronglyEncryptedInput());
        doTestBurst();
    }

    @Test
    public void splitWithOutline() throws IOException {
        setUpParameters();
        parameters.addSource(largeOutlineInput());
        parameters.addPage(1);
        parameters.addPage(2);
        parameters.addPage(3);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).forEachPdfOutput(d -> {
            assertTrue(nonNull(d.getDocumentCatalog().getDocumentOutline()));
        });
    }

    @Test
    public void splitWithDiscardOutline() throws IOException {
        setUpParameters();
        parameters.addSource(largeOutlineInput());
        parameters.discardOutline(true);
        parameters.addPage(1);
        parameters.addPage(2);
        parameters.addPage(3);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).forEachPdfOutput(d -> {
            assertTrue(isNull(d.getDocumentCatalog().getDocumentOutline()));
        });
    }

    public void doTestBurst() throws IOException {
        parameters.addPage(1);
        parameters.addPage(2);
        parameters.addPage(3);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
    }

    @Test
    public void even() throws IOException {
        setUpParameters();
        parameters.addSource(shortInput());
        doTestEven();
    }

    @Test
    public void evenEnc() throws IOException {
        setUpParameters();
        parameters.addSource(encryptedInput());
        doTestEven();
    }

    public void doTestEven() throws IOException {
        parameters.addPage(2);
        parameters.addPage(4);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void odd() throws IOException {
        setUpParameters();
        parameters.addSource(shortInput());
        doTestOdd();
    }

    @Test
    public void oddEnc() throws IOException {
        setUpParameters();
        parameters.addSource(encryptedInput());
        doTestOdd();
    }

    public void doTestOdd() throws IOException {
        parameters.addPage(1);
        parameters.addPage(3);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
    }

    @Test
    public void batchMode() throws IOException {
        setUpParameters();
        parameters.addSource(shortInput());
        parameters.addSource(mediumInput());
        parameters.addPage(2);

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
        testContext.assertOutputContainsFilenames("1_short-test-file.pdf", "3_short-test-file.pdf",
                "1_medium-test-file.pdf", "3_medium-test-file.pdf");
    }

    @Test
    public void batchModeSpecificFilenames() throws IOException {
        setUpParameters();
        parameters.addSource(shortInput());
        parameters.addSource(mediumInput());
        parameters.addPage(2);
        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        parameters.addSpecificResultFilename("three");
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertOutputContainsFilenames("one.pdf", "two.pdf", "three.pdf");
    }

    @Test
    public void specificResultFilenames() throws IOException {
        setUpParameters();
        parameters.addSource(mediumInput());
        parameters.addPage(2);
        parameters.addPage(5);
        parameters.addPage(7);

        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        parameters.addSpecificResultFilename("three");

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
        testContext.assertOutputContainsFilenames("one.pdf", "two.pdf", "three.pdf", "8_medium-test-file.pdf");
    }

    @Test
    public void specificResultFilenames_invalidCharacters() throws IOException {
        setUpParameters();
        parameters.addSource(mediumInput());
        parameters.addPage(1);
        parameters.addPage(2);

        parameters.addSpecificResultFilename("path/to/somewhere");
        parameters.addSpecificResultFilename("\\/:*?\"<>|`&aaa");

        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
        testContext.assertOutputContainsFilenames("pathtosomewhere.pdf", "`&aaa.pdf", "3_medium-test-file.pdf");
    }

    @Test
    public void sharedResourcesAreNotPurged() throws IOException {
        setUpParameters();
        parameters.addSource(customInput("/pdf/pages-shared-res1-forms-not-shared.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.addPage(1);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forEachPdfOutput(d -> {
            PDResources res = d.getPage(0).getResources();
            for (COSName name : res.getXObjectNames()) {
                try {
                    PDFormXObject form = (PDFormXObject) res.getXObject(name);
                    PDResources formRes = form.getResources();
                    Assertions.assertTrue(formRes.getXObjectNames().iterator().hasNext());
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        });
    }

    @Override
    public Task<SplitByPagesParameters> getTask() {
        return new SplitByPageNumbersTask<>();
    }
}
