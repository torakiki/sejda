/*
 * Created on 22/ago/2011
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.sejda.model.output.ExistingOutputPolicy;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfUnpackerTest {

    private PdfUnpacker victim = new PdfUnpacker(ExistingOutputPolicy.OVERWRITE);
    private InputStream is;
    private MultipleOutputWriter outputWriter;

    @Before
    public void setUp() {
        is = getClass().getClassLoader().getResourceAsStream("pdf/attachments_as_annots.pdf");
        outputWriter = spy(OutputWriters.newMultipleOutputWriter(ExistingOutputPolicy.OVERWRITE));
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
