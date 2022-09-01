/*
 * Created on 26/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Test;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.MultipleImageConversionTaskTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Andrea Vacondio
 */
public class PdfToMultipleTiffSamboxTaskTest extends MultipleImageConversionTaskTest<PdfToMultipleTiffParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToMultipleTiffSamboxTaskTest.class);

    @Override
    public PdfToMultipleTiffParameters getMultipleImageParametersWithoutSource(ImageColorType type) {
        PdfToMultipleTiffParameters parameters = new PdfToMultipleTiffParameters(type);
        parameters.setCompressionType(TiffCompressionType.PACKBITS);
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(96);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    @Test
    public void colorAndCompressionCombinations() throws IOException {
        for (ImageColorType type : ImageColorType.values()) {
            for (TiffCompressionType compression : TiffCompressionType.values()) {
                LOG.debug("Testing compression: {} and color type: {}", compression, type);

                boolean unsupportedCombo = false;
                if (compression == TiffCompressionType.CCITT_GROUP_3_1D
                        || compression == TiffCompressionType.CCITT_GROUP_3_2D
                        || compression == TiffCompressionType.CCITT_GROUP_4) {
                    if (type != ImageColorType.BLACK_AND_WHITE) {
                        unsupportedCombo = true;
                    }
                }

                if (unsupportedCombo) {
                    LOG.debug("Unsupported combination: compression: {} and color type: {}", compression, type);
                    continue;
                }

                PdfToMultipleTiffParameters parameters = getMultipleImageParametersWithoutSource(type);
                parameters.addSource(shortInput());
                parameters.addPageRange(new PageRange(1, 1));
                parameters.setCompressionType(compression);
                testContext.directoryOutputTo(parameters);
                execute(parameters);
                testContext.assertTaskCompleted();
                testContext.assertOutputSize(1);
            }
        }
    }

    @Override
    public Task<PdfToMultipleTiffParameters> getTask() {
        return new PdfToMultipleImageTask<>();
    }
}
