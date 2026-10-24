/*
 * Created on 13 gen 2016
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
import org.sejda.model.parameter.SplitByEveryXPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.tests.TestUtils.customInput;
import static org.sejda.tests.TestUtils.largeOutlineInput;
import static org.sejda.tests.TestUtils.mediumInput;
import static org.sejda.tests.TestUtils.stronglyEncryptedInput;

/**
 * @author Andrea Vacondio
 */
public class SplitByEveryXPageSamboxTaskTest extends BaseTaskTest<SplitByEveryXPagesParameters> {
    private SplitByEveryXPagesParameters parameters;

    @Test
    public void split() throws IOException {
        parameters = new SplitByEveryXPagesParameters(10);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
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
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
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
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("pdf/shared_resource_dic_w_images.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void splitOptimizeSharedXObjects() throws IOException {
        parameters = new SplitByEveryXPagesParameters(1);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("pdf/shared_xobjects_dics.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void splitOptimizeSharedFonts() throws IOException {
        parameters = new SplitByEveryXPagesParameters(1);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("pdf/shared_fonts.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
    }

    @Test
    public void splitOptimizeChangesSurviveObjectStreamReparse() throws IOException {
        // the pages share a /Group (X) that is in the same object stream as the /Resources of the form XObject (Y)
        // drawn by page 2. The optimization removes the unused F2 font from Y, writing the first output releases X
        // and writing the second one resolves X again, parsing the object stream again. Form XObject /Resources should
        // not go back to the original version.
        parameters = new SplitByEveryXPagesParameters(1);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(customInput("pdf/objstm_shared_group.pdf", "objstm_shared_group.pdf"));
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forPdfOutput("2_objstm_shared_group.pdf", d -> {
            try {
                var form = (PDFormXObject) d.getPage(0).getResources().getXObject(COSName.getPDFName("Fm1"));
                var fonts = form.getResources().getCOSObject().getDictionaryObject(COSName.FONT, COSDictionary.class);
                assertTrue(fonts.containsKey(COSName.getPDFName("F1")));
                assertFalse(fonts.containsKey(COSName.getPDFName("F2")));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void splitWithOutline() throws IOException {
        parameters = new SplitByEveryXPagesParameters(3);
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
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
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
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
        parameters.setCompressionPolicy(CompressionPolicy.COMPRESS);
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
