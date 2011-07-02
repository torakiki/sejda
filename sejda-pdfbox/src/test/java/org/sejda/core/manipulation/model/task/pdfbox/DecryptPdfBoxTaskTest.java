/*
 * Created on 03/nov/2010
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
package org.sejda.core.manipulation.model.task.pdfbox;

import java.io.InputStream;

import org.junit.Before;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.service.DecryptTaskTest;

/**
 * @author Andrea Vacondio
 * 
 */
public class DecryptPdfBoxTaskTest extends DecryptTaskTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_test_test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "enc_test_test_file.pdf", "test");
        getParameters().clearSourceList();
        getParameters().addSource(source);
    }

    public Task<DecryptParameters> getTask() {
        return new DecryptTask();
    }

}
