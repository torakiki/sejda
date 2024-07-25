/*
 * Created on 03 feb 2017
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

import org.sejda.model.image.ImageColorType;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.image.PdfToPngParameters;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.MultipleImageConversionTaskTest;

/**
 * @author Andrea Vacondio
 */
public class PdfToMultiplePngSamboxTaskTest extends MultipleImageConversionTaskTest<PdfToPngParameters> {

    @Override
    public PdfToPngParameters getMultipleImageParametersWithoutSource(ImageColorType type) {
        PdfToPngParameters parameters = new PdfToPngParameters(type);
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(300);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    @Override
    public Task<PdfToPngParameters> getTask() {
        return new PdfToMultipleImageTask();
    }
}
