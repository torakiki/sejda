/*
 * Created on Oct 2, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.cli.command.TestableTask;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.parameter.image.PdfToTiffParameters;

/**
 * Tests for tasks having pdf as input and tiff image format as output
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfToTiffTraitTest extends AbstractTaskTraitTest {

    public PdfToTiffTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    private static final TiffCompressionType DEFAULT_COMPRESSION_TYPE = TiffCompressionType.NONE;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { StandardTestableTask.PDF_TO_MULTIPLE_TIFF },
                { StandardTestableTask.PDF_TO_SINGLE_TIFF } });
    }

    @Test
    public void compressionType_default() {
        PdfToTiffParameters result = defaultCommandLine().without("--compressionType").invokeSejdaConsole();
        assertThat(result.getCompressionType(), is(DEFAULT_COMPRESSION_TYPE));
    }

    @Test
    public void compressionType() {
        PdfToTiffParameters result = defaultCommandLine().with("--compressionType", "jpeg_ttn2").invokeSejdaConsole();
        assertThat(result.getCompressionType(), is(TiffCompressionType.JPEG_TTN2));
    }

}
