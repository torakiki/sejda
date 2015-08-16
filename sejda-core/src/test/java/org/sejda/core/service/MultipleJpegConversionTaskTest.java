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

import org.junit.Ignore;
import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class MultipleJpegConversionTaskTest extends MultipleImageConversionTaskTest<PdfToJpegParameters> {

    @Override
    PdfToJpegParameters getMultipleImageParametersWithoutSource() {
        PdfToJpegParameters parameters = new PdfToJpegParameters();
        parameters.setOutputPrefix("[CURRENTPAGE]");
        parameters.setResolutionInDpi(300);
        parameters.setUserZoom(1.5f);
        parameters.setOverwrite(true);
        return parameters;
    }

}
