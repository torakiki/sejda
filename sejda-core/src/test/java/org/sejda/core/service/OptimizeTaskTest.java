/*
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
package org.sejda.core.service;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.support.io.IOUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class OptimizeTaskTest extends PdfOutEnabledTest implements TestableTask<OptimizeParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private OptimizeParameters parameters;

    private File outputFolder;

    @Before
    public void setUp() throws TaskException {
        outputFolder = IOUtils.createTemporaryFolder();
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
    }

    @After
    public void tearDown() throws IOException {
        //System.out.println(outputFolder);
        FileUtils.deleteDirectory(outputFolder);
    }

    private void setUpParameters() {
        parameters = new OptimizeParameters();
        parameters.setCompressImages(true);
        parameters.setImageQuality(0.8f);
        parameters.setImageDpi(72);
        parameters.setImageMaxWidthOrHeight(1280);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutput(new DirectoryTaskOutput(outputFolder));
    }

    private void withSource(String input) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(input);
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_unoptimized.pdf");
        parameters.addSource(source);
    }

    private long sizeOfResult() {
        return outputFolder.listFiles()[0].length() / 1000;
    }

    @Test
    public void testBasics() throws TaskException, IOException {
        withSource("pdf/unoptimized.pdf");
        victim.execute(parameters);
        assertThat(sizeOfResult(), is(lessThan(104L)));
    }

    @Test
    public void testRepeatedImages() throws TaskException, IOException {
        withSource("pdf/test_optimize_repeated_images.pdf");
        victim.execute(parameters);
        assertThat(sizeOfResult(), is(lessThan(468L)));
    }
}
