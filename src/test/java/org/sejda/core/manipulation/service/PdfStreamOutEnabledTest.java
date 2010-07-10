/*
 * Created on 04/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.sejda.core.Sejda;
import org.sejda.core.manipulation.model.output.PdfStreamOutput;
import org.sejda.core.manipulation.model.parameter.AbstractParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Parent test class with common methods to read results or common asserts.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public class PdfStreamOutEnabledTest {

    private ByteArrayOutputStream out;

    /**
     * Initialize the input parameters with a new {@link PdfStreamOutput}
     * 
     * @param parameters
     */
    void initializeNewStreamOutput(AbstractParameters parameters) {
        out = new ByteArrayOutputStream();
        PdfStreamOutput pdfOut = new PdfStreamOutput(out);
        parameters.setOutput(pdfOut);
    }

    /**
     * @param expectedFileName
     *            the expected name of the first file in the ZipInputStream
     * @return a {@link PdfReader} opened on the first resulting file found in the ZipInputStream coming form the manipulation.
     * @throws IOException
     */
    PdfReader getReaderFromResult(String expectedFileName) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry = zip.getNextEntry();
        if (StringUtils.isNotBlank(expectedFileName)) {
            assertEquals(expectedFileName, entry.getName());
        }
        return new PdfReader(zip);
    }

    /**
     * @return a {@link PdfReader} opened on the first resulting file found in the ZipInputStream coming form the manipulation.
     * @throws IOException
     */
    PdfReader getReaderFromResult() throws IOException {
        return getReaderFromResult(null);
    }

    /**
     * Assert the correct creator
     * 
     * @param reader
     */
    void assertCreator(PdfReader reader) {
        HashMap<String, String> meta = reader.getInfo();
        assertEquals(Sejda.CREATOR, meta.get(PdfMetadataKey.CREATOR.getKey()));
    }
}
