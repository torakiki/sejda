/*
 * Created on 03 feb 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.cli.command.TestableTask;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.PdfToPngParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfToPngTraitTest extends AbstractTaskTraitTest {
    public PdfToPngTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { StandardTestableTask.PDF_TO_PNG } });
    }

    @Test
    public void defaultParams() {
        PdfToPngParameters result = defaultCommandLine().without("-r").invokeSejdaConsole();
        assertThat(result.getResolutionInDpi(), is(AbstractPdfToImageParameters.DEFAULT_DPI));
    }

    @Test
    public void resolution() {
        PdfToPngParameters result = defaultCommandLine().with("-r", "90").invokeSejdaConsole();
        assertThat(result.getResolutionInDpi(), is(90));
    }

    @Test
    public void pageRanges() {
        PdfToPngParameters parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-").invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }
}
