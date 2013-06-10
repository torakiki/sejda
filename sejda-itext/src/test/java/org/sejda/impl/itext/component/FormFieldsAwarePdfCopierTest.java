/*
 * Created on 08/giu/2013
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class FormFieldsAwarePdfCopierTest {
    private File outFile;

    @Before
    public void setUp() throws IOException {
        outFile = File.createTempFile("sejdaTest", ".pdf");
        outFile.deleteOnExit();
    }

    @After
    public void tearDown() {
        outFile.delete();
    }

    @Test
    public void testCount() throws IOException, TaskException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
            reader = new PdfReader(inputStream);
            reader.selectPages("2-3");
            FormFieldsAwarePdfCopier victim = new FormFieldsAwarePdfCopier(outFile, PdfVersion.VERSION_1_5);
            victim.addAllPages(reader);
            assertEquals(2, victim.getNumberOfCopiedPages());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }
}
