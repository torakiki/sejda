/*
 * Created on 09/lug/2010
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
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

/**
 * Test unit for the set metadata task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
@SuppressWarnings("unchecked")
public abstract class SetMetadataTaskTest extends PdfOutEnabledTest implements TestableTask<SetMetadataParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetMetadataParameters parameters = new SetMetadataParameters();

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set metadata parameters
     * 
     */
    private void setUpParameters() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        setUpParams(source);
    }

    private void setUpParametersEncrypted() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_with_modify_perm.pdf", "test");
        setUpParams(source);
    }

    private void setUpParams(PdfSource<?> source) {
        parameters.setCompress(true);
        parameters.setOutputName("outName.pdf");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.put(PdfMetadataKey.AUTHOR, "test_author");
        parameters.put(PdfMetadataKey.KEYWORDS, "test_keywords");
        parameters.put(PdfMetadataKey.SUBJECT, "test_subject");
        parameters.put(PdfMetadataKey.TITLE, "test_title");
        parameters.setSource(source);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        setUpParameters();
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        setUpParametersEncrypted();
        doExecute();
    }

    private void doExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertVersion(reader, PdfVersion.VERSION_1_6);
        HashMap<String, String> meta = reader.getInfo();
        assertEquals("test_author", meta.get(PdfMetadataKey.AUTHOR.getKey()));
        assertEquals("test_keywords", meta.get(PdfMetadataKey.KEYWORDS.getKey()));
        assertEquals("test_subject", meta.get(PdfMetadataKey.SUBJECT.getKey()));
        assertEquals("test_title", meta.get(PdfMetadataKey.TITLE.getKey()));
        reader.close();
    }

    protected SetMetadataParameters getParameters() {
        return parameters;
    }

}
