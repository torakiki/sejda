/*
 * Created on 22/ago/2011
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
package org.sejda.impl.itext.component;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.exception.TaskException;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfUnpackerTest {

    private PdfUnpacker victim = new PdfUnpacker();
    private InputStream is;
    private MultipleOutputWriter outputWriter;

    @Before
    public void setUp() {
        is = getClass().getClassLoader().getResourceAsStream("pdf/attachments.pdf");
        outputWriter = spy(OutputWriters.newMultipleOutputWriter());
        TestUtils.setProperty(victim, "outputWriter", outputWriter);
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(is);
    }

    @Test
    public void testUnpack() throws IOException, TaskException {
        PdfReader reader = new PdfReader(is);
        victim.unpack(reader);
        verify(outputWriter).addOutput(any(PopulatedFileOutput.class));
    }

    @Test(expected = TaskException.class)
    public void testUnpackNulll() throws TaskException {
        victim.unpack(null);
    }
}
