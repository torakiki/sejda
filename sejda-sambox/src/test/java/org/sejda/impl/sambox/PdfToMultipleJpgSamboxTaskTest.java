/*
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
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.MultipleImageConversionTaskTest;

import java.io.IOException;

import static org.sejda.tests.TestUtils.shortInput;

public class PdfToMultipleJpgSamboxTaskTest extends MultipleImageConversionTaskTest<PdfToJpegParameters> {

    @Override
    public PdfToJpegParameters getMultipleImageParametersWithoutSource(ImageColorType type) {
        PdfToJpegParameters parameters = new PdfToJpegParameters(type);
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(300);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    @Test
    public void colorAndCompressionCombinations() throws IOException {
        for (ImageColorType type : ImageColorType.values()) {
            PdfToJpegParameters parameters = getMultipleImageParametersWithoutSource(type);
            parameters.addSource(shortInput());
            parameters.addPageRange(new PageRange(1, 1));
            parameters.setQuality(35);
            doExecute(parameters, 1);
        }
    }

    @Test
    public void noPages() throws IOException {
        PdfToJpegParameters parameters = new PdfToJpegParameters(ImageColorType.COLOR_RGB);
        parameters.addSource(shortInput());
        parameters.addPageRange(new PageRange(100, 200));
        testContext.directoryOutputTo(parameters);

        testContext.listenForTaskFailure();
        execute(parameters);
        testContext.assertTaskFailed("No pages converted");
    }

    @Override
    public Task<PdfToJpegParameters> getTask() {
        return new PdfToMultipleImageTask();
    }
}
