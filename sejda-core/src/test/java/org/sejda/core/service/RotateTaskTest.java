/*
 * Created on 13/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract test unit for the rotate task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
@SuppressWarnings("unchecked")
public abstract class RotateTaskTest extends PdfOutEnabledTest implements TestableTask<RotateParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private RotateParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpDefaultParameters() {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    private void setUpParametersWithVersionPrefixAndCompressionSpecified() {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_4);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    private void setUpRotateSpecificPages() {
        parameters = new RotateParameters(Rotation.DEGREES_90);
        parameters.addPageRange(new PageRange(2, 4));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    private void setUpRotateMultipleInputNotRangesContained() {
        parameters = new RotateParameters(Rotation.DEGREES_90);
        parameters.addPageRange(new PageRange(2, 4));
        parameters.addPageRange(new PageRange(15, 15));
        parameters.addSource(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf"), "test_file.pdf"));
        parameters.addSource(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/medium_test.pdf"), "medium_test.pdf"));
        parameters.setOverwrite(true);
    }

    private void setUpParametersEncrypted() {
        setUpDefaultParameters();

        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "test_file.pdf", "test");
        parameters.addSource(source);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        setUpDefaultParameters();
        doExecute();

        PdfReader reader = getReaderFromResultZipStream("test_file.pdf");
        assertEquals(4, reader.getNumberOfPages());
        assertEquals(180, reader.getPageRotation(2));
        reader.close();
    }

    @Test
    public void testRotateSpecificPages() throws TaskException, IOException {
        setUpRotateSpecificPages();
        doExecute();

        PdfReader reader = getReaderFromResultZipStream("test_file.pdf");
        assertEquals(90, reader.getPageRotation(3));
        reader.close();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        setUpParametersEncrypted();
        doExecute();

        PdfReader reader = getReaderFromResultZipStream("test_file.pdf");

        assertEquals(4, reader.getNumberOfPages());
        assertEquals(180, reader.getPageRotation(2));
        reader.close();
    }

    @Test
    public void testVersionPrefixAndCreatorAreApplied() throws TaskException, IOException {
        setUpParametersWithVersionPrefixAndCompressionSpecified();

        doExecute();

        PdfReader reader = getReaderFromResultZipStream("test_prefix_test_file.pdf");
        assertCreator(reader);
        // TODO it seems iText 2 reads the version from the header only while it should read from the catalog first so this assert fails in PDFBox 2
        // assertVersion(reader, PdfVersion.VERSION_1_4);
    }

    @Test
    public void testMultipleInputOneDoesntContainRange() throws TaskException, IOException {
        setUpRotateMultipleInputNotRangesContained();
        doExecute();

        assertOutputContainsDocuments(2);
    }

    private void doExecute() throws TaskException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
    }

}
