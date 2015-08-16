/*
 * Created on Oct 13, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.conversion;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.CombinableMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;

/**
 * Tests for {@link PdfFileSourceAdapter}
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfFileSourceAdapterTest {

    private File file;

    @Before
    public void setUp() {
        InputStream contents = getClass().getResourceAsStream("/pdf/test_outline.pdf");
        file = new File("/tmp/inputFile1.pdf");
        file.deleteOnExit();
        try {
            FileUtils.copyInputStreamToFile(contents, file);
        } catch (IOException e) {
            throw new SejdaRuntimeException("Can't create test file. Reason: " + e.getMessage(), e);
        }
    }

    @After
    public void tearDown() {
        if (file != null) {
            file.delete();
        }
    }

    @Test
    public void windowsFileWithPassword() {
        PdfFileSource result = new PdfFileSourceAdapter("/tmp/inputFile1.pdf:secret123").getPdfFileSource();
        assertThat(result.getPassword(), is("secret123"));
        assertThat(result.getSource(),
                CombinableMatcher.<File> either(is(new File("/tmp/inputFile1.pdf"))).or(
                        is(new File("c:\\tmp\\inputFile1.pdf"))));
    }

    @Test
    public void windowsFileNoPassword() {
        PdfFileSource result = new PdfFileSourceAdapter("/tmp/inputFile1.pdf").getPdfFileSource();
        assertNull(result.getPassword());
        assertThat(result.getSource(),
                CombinableMatcher.<File> either(is(new File("/tmp/inputFile1.pdf"))).or(
                        is(new File("c:\\tmp\\inputFile1.pdf"))));
    }

    @Test
    public void protectedFileWithPasswordContainingSeparator() {
        PdfFileSource result = new PdfFileSourceAdapter("/tmp/inputFile1.pdf:secret.pdf:password").getPdfFileSource();
        assertThat(result.getPassword(), is("secret.pdf:password"));
        assertThat(result.getSource(),
                CombinableMatcher.<File> either(is(new File("/tmp/inputFile1.pdf"))).or(
                        is(new File("c:\\tmp\\inputFile1.pdf"))));
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
