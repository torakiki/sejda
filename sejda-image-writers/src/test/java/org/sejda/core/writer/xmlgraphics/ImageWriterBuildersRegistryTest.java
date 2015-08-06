/*
 * Created on 24/set/2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.writer.xmlgraphics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.writer.model.ImageWriter.ImageWriterBuilder;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * @author Andrea Vacondio
 * 
 */
public class ImageWriterBuildersRegistryTest {

    private ImageWriterBuildersRegistry victim;

    @Before
    public void setUp() {
        victim = new ImageWriterBuildersRegistry();
    }

    @Test
    public void testRegistry() {
        @SuppressWarnings("unchecked")
        ImageWriterBuilder<PdfToMultipleTiffParameters> builder = mock(ImageWriterBuilder.class);
        victim.addBuilder(PdfToMultipleTiffParameters.class, builder);
        PdfToMultipleTiffParameters params = new PdfToMultipleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        assertNotNull(victim.getBuilder(params));
        PdfToSingleTiffParameters notAddedParams = new PdfToSingleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        assertNull(victim.getBuilder(notAddedParams));
    }
}
