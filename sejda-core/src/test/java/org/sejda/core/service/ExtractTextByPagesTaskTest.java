/*
 * Created on 10/25/13
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.core.support.io.IOUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.parameter.ExtractTextByPagesParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;

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
        parameters.setCompress(true);
        parameters.setTextEncoding("UTF-8");

    }

    @Test
    public void testExecuteWrongRange() throws TaskException {
        parameters.addPageRange(new PageRange(10));
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        victim.execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void testExecuteRange() throws TaskException, IOException {
        parameters.addPageRange(new PageRange(1, 3));
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        victim.execute(parameters);

        assertEquals(3, out.list().length);
        assertThat(out.list(), hasItemInArray("1_test_file.txt"));
        assertThat(out.list(), hasItemInArray("2_test_file.txt"));
        assertThat(out.list(), hasItemInArray("3_test_file.txt"));

        String contents1 = FileUtils.readFileToString(new File(out, "1_test_file.txt")).trim();
        assertThat(contents1, startsWith("GNU LIBRARY GENERAL PUBLIC LICENSE"));
        assertThat(contents1, endsWith("This"));

        String contents3 = FileUtils.readFileToString(new File(out, "3_test_file.txt")).trim();
        assertThat(contents3, startsWith("and installation of the library."));
        assertThat(contents3, endsWith("your rights to work written entirely by you; rather, the intent is to"));
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        victim.execute(parameters);

        assertEquals(4, out.list().length);
        assertThat(out.list(), hasItemInArray("1_test_file.txt"));
        assertThat(out.list(), hasItemInArray("2_test_file.txt"));
        assertThat(out.list(), hasItemInArray("3_test_file.txt"));
        assertThat(out.list(), hasItemInArray("4_test_file.txt"));

        String contents1 = FileUtils.readFileToString(new File(out, "1_test_file.txt")).trim();
        assertThat(contents1, startsWith("GNU LIBRARY GENERAL PUBLIC LICENSE"));
        assertThat(contents1, endsWith("This"));

        String contents3 = FileUtils.readFileToString(new File(out, "3_test_file.txt")).trim();
        assertThat(contents3, startsWith("and installation of the library."));
        assertThat(contents3, endsWith("your rights to work written entirely by you; rather, the intent is to"));
    }
}
