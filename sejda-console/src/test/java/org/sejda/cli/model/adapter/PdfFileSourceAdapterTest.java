/*
 * Created on Oct 13, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.model.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.either;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sejda.cli.AbstractTestSuite;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.input.PdfFileSource;

/**
 * Tests for {@link PdfFileSourceAdapter}
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfFileSourceAdapterTest extends AbstractTestSuite {

    @Before
    public void setUp() {
        createTestPdfFile("/tmp/inputFile1.pdf");
    }

    @Test
    public void windowsFileWithPassword() {
        PdfFileSource result = new PdfFileSourceAdapter("/tmp/inputFile1.pdf:secret123").getPdfFileSource();
        assertThat(result.getPassword(), is("secret123"));
        assertThat(result.getFile(),
                either(is(new File("/tmp/inputFile1.pdf"))).or(is(new File("c:\\tmp\\inputFile1.pdf"))));
    }

    @Test
    public void windowsFileNoPassword() {
        PdfFileSource result = new PdfFileSourceAdapter("/tmp/inputFile1.pdf").getPdfFileSource();
        assertNull(result.getPassword());
        assertThat(result.getFile(),
                either(is(new File("/tmp/inputFile1.pdf"))).or(is(new File("c:\\tmp\\inputFile1.pdf"))));
    }

    @Test
    public void protectedFileWithPasswordContainingSeparator() {
        PdfFileSource result = new PdfFileSourceAdapter("/tmp/inputFile1.pdf:secret.pdf:password").getPdfFileSource();
        assertThat(result.getPassword(), is("secret.pdf:password"));
        assertThat(result.getFile(),
                either(is(new File("/tmp/inputFile1.pdf"))).or(is(new File("c:\\tmp\\inputFile1.pdf"))));
    }

    @Test
    public void fileDoestExist() {
        try {
            new PdfFileSourceAdapter("/tmp/doesntexist.pdf:secret").getPdfFileSource();
        } catch (SejdaRuntimeException e) {
            assertThat(e.getMessage(), containsString("does not exist"));
        }
    }

}
