/*
 * Created on 08/mar/2013
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.ImageTestUtils;
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
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class MultipleImageConversionTaskTest<T extends AbstractPdfToMultipleImageParameters> implements
        TestableTask<T> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    abstract T getMultipleImageParametersWithoutSource();

    @Test
    public void testExecuteEncryptedStreamToMultipleImage() throws TaskException, IOException {
        AbstractPdfToMultipleImageParameters parameters = getMultipleImageParametersWithoutSource();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_test_test_file.pdf", "test");
        parameters.setSource(source);
        int converted = doExecute(parameters);
        assertEquals(4, converted);
    }

    @Test
    public void testExecuteStreamToMultipleImage() throws TaskException, IOException {
        AbstractPdfToMultipleImageParameters parameters = getMultipleImageParametersWithoutSource();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_jpg.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_jpg.pdf");
        parameters.setSource(source);
        int converted = doExecute(parameters);
        assertEquals(1, converted);
    }

    @Test
    public void testExecuteStreamToMultipleImageWithPageSelection() throws TaskException, IOException {
        AbstractPdfToMultipleImageParameters parameters = getMultipleImageParametersWithoutSource();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.addPageRange(new PageRange(2, 3));
        int converted = doExecute(parameters);
        assertEquals(2, converted);
    }

    @Test
    public void testWrongPageSelection() throws TaskException {
        AbstractPdfToMultipleImageParameters parameters = getMultipleImageParametersWithoutSource();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.addPageRange(new PageRange(10));
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        victim.execute(parameters);
        assertTrue(failListener.isFailed());
    }

    private int doExecute(AbstractPdfToMultipleImageParameters parameters) throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parameters.setOutput(new StreamTaskOutput(out));
        victim.execute(parameters);
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry = zip.getNextEntry();
        int entries = 0;
        while (entry != null) {
            RenderedImage ri = ImageTestUtils.loadImage(zip, entry.getName());
            assertTrue(ri.getHeight() > 0);
            assertTrue(ri.getWidth() > 0);
            zip.closeEntry();
            entry = zip.getNextEntry();
            entries++;
        }
        input.close();
        return entries;
    }

    @Ignore("In place of a better way to check image quality with automated tests")
    public void testImageConversion() throws TaskException {
        AbstractPdfToMultipleImageParameters parameters = getMultipleImageParametersWithoutSource();
        when(context.getTask(parameters)).thenReturn((Task) getTask());

        File out = IOUtils.createTemporaryFolder();
        parameters.setOutput(new DirectoryTaskOutput(out));
        victim.execute(parameters);
        System.out.println("Images generated to: " + out.getAbsolutePath());
    }
}
