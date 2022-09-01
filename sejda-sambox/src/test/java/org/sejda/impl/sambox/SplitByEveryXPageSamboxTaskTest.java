/*
 * Created on 13 gen 2016
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
import org.sejda.model.parameter.SplitByEveryXPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 */
public class SplitByEveryXPageSamboxTaskTest extends BaseTaskTest<SplitByEveryXPagesParameters> {
    private SplitByEveryXPagesParameters parameters;

    @Test
    public void split() throws IOException {
        parameters = new SplitByEveryXPagesParameters(10);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(mediumInput());
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
    }

    @Test
    public void splitEnc() throws IOException {
        parameters = new SplitByEveryXPagesParameters(2);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(stronglyEncryptedInput());
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void splitOptimizeShareResources() throws IOException {
        parameters = new SplitByEveryXPagesParameters(1);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("/pdf/shared_resource_dic_w_images.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void splitOptimizeSharedXObjects() throws IOException {
        parameters = new SplitByEveryXPagesParameters(1);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("/pdf/shared_xobjects_dics.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void splitOptimizeSharedFonts() throws IOException {
        parameters = new SplitByEveryXPagesParameters(1);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("/pdf/shared_fonts.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
    }

    @Test
    public void splitWithOutline() throws IOException {
        parameters = new SplitByEveryXPagesParameters(3);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(largeOutlineInput());
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2)
                .forEachPdfOutput(d -> assertTrue(nonNull(d.getDocumentCatalog().getDocumentOutline())));
    }

    @Test
    public void splitWithDiscardOutline() throws IOException {
        parameters = new SplitByEveryXPagesParameters(3);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(largeOutlineInput());
        parameters.discardOutline(true);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2)
                .forEachPdfOutput(d -> assertTrue(isNull(d.getDocumentCatalog().getDocumentOutline())));
    }

    @Test
    public void specificResultFilenames() throws IOException {
        parameters = new SplitByEveryXPagesParameters(10);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(mediumInput());
        testContext.directoryOutputTo(parameters);
        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        parameters.addSpecificResultFilename("some/*?Invalid<chars");
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertOutputContainsFilenames("one.pdf", "two.pdf", "someInvalidchars.pdf");
    }

    @Override
    public Task<SplitByEveryXPagesParameters> getTask() {
        return new SplitByPageNumbersTask<>();
    }
}
