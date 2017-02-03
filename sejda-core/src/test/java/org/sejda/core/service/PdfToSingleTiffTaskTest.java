/*
 * Created on 16/set/2011
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class PdfToSingleTiffTaskTest extends BaseTaskTest<PdfToSingleTiffParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToSingleTiffTaskTest.class);

    private AbstractPdfToSingleImageParameters getSingleTiffParams() {
        PdfToSingleTiffParameters parameters = new PdfToSingleTiffParameters(ImageColorType.GRAY_SCALE);
        parameters.setCompressionType(TiffCompressionType.PACKBITS);
        setCommonParams(parameters);
        return parameters;
    }

    private void setCommonParams(AbstractPdfToImageParameters parameters) {
        parameters.setResolutionInDpi(96);
        parameters.setSource(customEncryptedInput("pdf/enc_test_test_file.pdf", "test"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    public void testExecuteStreamToSingleTiff() throws IOException {
        AbstractPdfToSingleImageParameters parameters = getSingleTiffParams();
        testContext.fileOutputTo(parameters, ".tiff");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forRawOutput(p -> {
            try {
                RenderedImage ri = ImageIO.read(p.toFile());
                assertTrue(ri.getHeight() > 0);
                assertTrue(ri.getWidth() > 0);
            } catch (Exception e) {
                LOG.error("Test failed", e);
                fail();
            }
        });
    }

}
