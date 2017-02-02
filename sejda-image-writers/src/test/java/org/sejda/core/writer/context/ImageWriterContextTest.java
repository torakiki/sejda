/*
 * Created on 25/set/2011
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
package org.sejda.core.writer.context;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * @author Andrea Vacondio
 * 
 */
public class ImageWriterContextTest {

    @Test
    public void validParams() throws TaskException {
        assertNotNull(
                ImageWriterContext.getContext().createImageWriter(new PdfToJpegParameters(ImageColorType.COLOR_RGB)));
    }

    @Test(expected = TaskException.class)
    public void invalidParamsClass() throws Exception {
        ImageWriterContext.getContext().createImageWriter(mock(AbstractPdfToSingleImageParameters.class));
    }
}
