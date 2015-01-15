/*
 * Created on 13/mar/2013
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
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfToJpegTraitTest extends AbstractTaskTraitTest {
    public PdfToJpegTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { TestableTask.PDF_TO_JPEG } });
    }

    @Test
    public void defaultParams() {
        PdfToJpegParameters result = defaultCommandLine().without("-r").invokeSejdaConsole();
        assertThat(result.getResolutionInDpi(), is(AbstractPdfToImageParameters.DEFAULT_DPI));
        assertThat(result.getUserZoom(), is(1f));
    }

    @Test
    public void resolution() {
        PdfToJpegParameters result = defaultCommandLine().with("-r", "90").invokeSejdaConsole();
        assertThat(result.getResolutionInDpi(), is(90));
    }

    @Test
    public void userZoom() {
        PdfToJpegParameters result = defaultCommandLine().with("-z", "1.5").invokeSejdaConsole();
        assertThat(result.getUserZoom(), is(1.5f));
    }

    @Test
    public void pageRanges() {
        PdfToJpegParameters parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-")
                .invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }
}
