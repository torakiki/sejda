/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.pdf.PdfVersion;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SplitBySizeTaskTest extends BaseTaskTest<SplitBySizeParameters> {

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
        testContext.assertOutputContainsFilenames("1-medium-test-file.pdf", "2-medium-test-file.pdf", "3-medium-test-file.pdf", "4-medium-test-file.pdf","5-test-file.pdf");
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
}
