/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import com.lowagie.text.pdf.PdfReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SetFooterParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.footer.FooterNumberingStyle;
import org.sejda.model.pdf.footer.PdfFooterLabel;
import org.sejda.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Eduard Weissmann
 * 
 */
@Ignore
public abstract class SetFooterTaskTest extends PdfOutEnabledTest implements TestableTask<SetFooterParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetFooterParameters parameters;

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
        parameters = new SetFooterParameters();
        PdfFooterLabel label1 = PdfFooterLabel.newInstanceTextOnly("Introduction");
        PdfFooterLabel label3 = PdfFooterLabel.newInstanceNoLabelPrefix(FooterNumberingStyle.ARABIC, 100);
        parameters.putLabel(1, label1);
        parameters.putLabel(3, label3);

        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);

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

        reader.close();

        assertSpecificFooterExpectations(getResultFile());
    }

    protected abstract void assertSpecificFooterExpectations(File result) throws TaskException, IOException;

    protected SetFooterParameters getParameters() {
        return parameters;
    }

}
