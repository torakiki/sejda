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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.cli.command.TestableTask;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;

/**
 * Test verifying that the --colorType and --resolution flags can be specified for each task creating image outputs
 * 
 * @author Eduard Weissmann
 * 
 */
public class ImageFormatOutputTraitTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { StandardTestableTask.PDF_TO_SINGLE_TIFF } });
    }

    public ImageFormatOutputTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void colorType() {
        AbstractPdfToImageParameters result = defaultCommandLine().with("--colorType", "BLACK_AND_WHITE")
                .invokeSejdaConsole();

        assertThat(describeExpectations(), result.getOutputImageColorType(), is(ImageColorType.BLACK_AND_WHITE));
    }

    @Test
    public void defaultColorType() {
        AbstractPdfToImageParameters result = defaultCommandLine().without("--colorType").invokeSejdaConsole();

        assertThat(describeExpectations(), result.getOutputImageColorType(), is(ImageColorType.COLOR_RGB));
    }

    @Test
    public void resolution() {
        AbstractPdfToImageParameters result = defaultCommandLine().with("--resolution", "150").invokeSejdaConsole();

        assertThat(describeExpectations(), result.getResolutionInDpi(), is(150));
    }

    @Test
    public void resolution_isOptional() {
        AbstractPdfToImageParameters result = defaultCommandLine().without("--resolution").invokeSejdaConsole();

        assertNotNull(describeExpectations(), result);
    }
}
