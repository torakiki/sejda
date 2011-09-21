/*
 * Created on 20/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.writer.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.sejda.core.manipulation.model.image.ImageColorType;
import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.core.manipulation.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.core.manipulation.model.parameter.image.PdfToSingleTiffParameters;

/**
 * @author Andrea Vacondio
 * 
 */
public class XmlGraphicsImageWriterFactoryTest {

    private XmlGraphicsImageWriterFactory victim = new XmlGraphicsImageWriterFactory();

    @Test
    public void testCreateImageWriter() {
        PdfToMultipleTiffParameters multipleTiff = new PdfToMultipleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        assertNotNull(victim.createImageWriter(multipleTiff));
        PdfToSingleTiffParameters singleTiff = new PdfToSingleTiffParameters(ImageColorType.BLACK_AND_WHITE);
        assertNotNull(victim.createImageWriter(singleTiff));
        AbstractPdfToImageParameters noBuilder = mock(AbstractPdfToImageParameters.class);
        assertNull(victim.createImageWriter(noBuilder));
    }
}
