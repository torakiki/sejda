/*
 * Created on 08/giu/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
