/*
 * Created on 23/gen/2011
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
 * GNU General Public License for more details.
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
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfPageLabels.PdfPageLabelFormat;
import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SetPagesLabelTaskTest extends PdfOutEnabledTest implements TestableTask<SetPagesLabelParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetPagesLabelParameters parameters;

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set page labels parameters
     * 
     */
    private void setUpParameters() {
        parameters = new SetPagesLabelParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.putLabel(1, PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 1));
        parameters.putLabel(3, PdfPageLabel.newInstanceWithLabel("Test", PdfLabelNumberingStyle.ARABIC, 1));
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
        assertVersion(reader, PdfVersion.VERSION_1_6);
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
