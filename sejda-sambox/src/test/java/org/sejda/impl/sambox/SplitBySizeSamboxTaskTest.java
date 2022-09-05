/*
 * Created on 31/ago/2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;
import java.nio.file.Files;

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
        parameters.setCompress(true);
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
        parameters.setCompress(true);
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
        parameters.setCompress(true);
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
        parameters.setCompress(true);
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
        parameters.setCompress(true);
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

    @Override
    public Task<SplitBySizeParameters> getTask() {
        return new SplitBySizeTask();
    }

}