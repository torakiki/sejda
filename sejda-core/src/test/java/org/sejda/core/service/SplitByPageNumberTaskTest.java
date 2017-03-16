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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitByPagesParameters;
import org.sejda.model.pdf.PdfVersion;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SplitByPageNumberTaskTest extends BaseTaskTest<SplitByPagesParameters> {
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
        parameters.addSource(customInput("pdf/shared_resource_dic_w_images.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.addPage(1);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void burstOptimizeFonts() throws IOException {
        setUpParameters();
        parameters.addSource(customInput("pdf/shared_fonts.pdf"));
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
        testContext.assertOutputContainsFilenames("1_short-test-file.pdf", "3_short-test-file.pdf", "1_medium-test-file.pdf", "3_medium-test-file.pdf");
    }
}
