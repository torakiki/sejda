/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.service;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.SetPagesLabelParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.core.manipulation.model.pdf.label.PdfPageLabel;
import org.sejda.core.manipulation.model.task.Task;

import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfPageLabels.PdfPageLabelFormat;
import com.itextpdf.text.pdf.PdfReader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SetPagesLabelTaskTest extends PdfOutEnabledTest implements TestableTask<SetPagesLabelParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private SetPagesLabelParameters parameters;

    @Before
    public void setUp() throws TaskException {
        setUpParameters();
        victim.setContext(context);
    }

    /**
     * Set up of the set page labels parameters
     * 
     */
    private void setUpParameters() {
        parameters = new SetPagesLabelParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.add(PdfPageLabel.newInstance(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 1));
        parameters.add(PdfPageLabel.newInstanceWithLabelAndLogicalNumber("Test", PdfLabelNumberingStyle.ARABIC, 3, 1));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(4, reader.getNumberOfPages());
        PdfPageLabelFormat[] formats = PdfPageLabels.getPageLabelFormats(reader);
        assertEquals(1, formats[0].logicalPage);
        assertEquals(1, formats[1].logicalPage);
        assertEquals(1, formats[0].physicalPage);
        assertEquals(3, formats[1].physicalPage);
        assertEquals(PdfPageLabels.LOWERCASE_ROMAN_NUMERALS, formats[0].numberStyle);
        assertEquals(PdfPageLabels.DECIMAL_ARABIC_NUMERALS, formats[1].numberStyle);
        assertEquals("Test", formats[1].prefix);
        reader.close();
    }

    protected SetPagesLabelParameters getParameters() {
        return parameters;
    }

}
