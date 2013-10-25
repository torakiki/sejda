/*
 * Created on 10/25/13
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.support.io.IOUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.parameter.ExtractTextByPagesParameters;
import org.sejda.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base tests for testing the ExtractTextByPages task.
 *
 * @author Edi Weissmann
 */
@Ignore
public abstract class ExtractTextByPagesTaskTest implements TestableTask<ExtractTextByPagesParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private ExtractTextByPagesParameters parameters;
    private File out = IOUtils.createTemporaryFolder();

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters() {
        parameters = new ExtractTextByPagesParameters();
        parameters.setOutput(new DirectoryTaskOutput(out));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
        parameters.setTextEncoding("UTF-8");
        parameters.addPage(1);
        parameters.addPage(3);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        victim.execute(parameters);

        assertEquals(2, out.list().length);
        assertThat(out.list(), hasItemInArray("1_test_file.txt"));
        assertThat(out.list(), hasItemInArray("3_test_file.txt"));

        String contents1 = FileUtils.readFileToString(new File(out, "1_test_file.txt")).trim();
        assertThat(contents1, startsWith("GNU LIBRARY GENERAL PUBLIC LICENSE"));
        assertThat(contents1, endsWith("interface definition files, plus the scripts used to control compilation"));

        String contents3 = FileUtils.readFileToString(new File(out, "3_test_file.txt")).trim();
        assertThat(contents3, startsWith("and installation of the library."));
        assertThat(contents3, endsWith("END OF TERMS AND CONDITIONS"));
    }
}
