/*
 * Created on 08/mar/2013
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
package org.sejda.core.service;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class PdfToMultipleJpegTaskTest extends MultipleImageConversionTaskTest<PdfToJpegParameters> {

    @Override
    PdfToJpegParameters getMultipleImageParametersWithoutSource(ImageColorType type) {
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

        testContext.expectTaskWillFail();
        execute(parameters);
        testContext.assertTaskFailed("No pages converted");
    }
}
