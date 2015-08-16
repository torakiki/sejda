/*
 * Created on 24/nov/2011
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
package org.sejda.impl.itext.component.split;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.impl.itext.util.ITextUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.parameter.SimpleSplitParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.task.NotifiableTaskMetadata;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class PagesPdfSplitterTest {

    private SimpleSplitParameters params = new SimpleSplitParameters(PredefinedSetOfPages.ALL_PAGES);
    private PdfReader reader;
    private PdfSource source;

    @Before
    public void setUp() {
        source = mock(PdfSource.class);
        when(source.getName()).thenReturn("name");
        params.setSource(source);
    }

    @After
    public void tearDown() {
        ITextUtils.nullSafeClosePdfReader(reader);
    }

    @Test(expected = OutOfMemoryError.class)
    // issue #80
    public void testFinallyDoesntSwallowErrors() throws IOException, TaskException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = new PdfReader(inputStream);
            PagesPdfSplitter<AbstractSplitByPageParameters> victim = spy(new PagesPdfSplitter<AbstractSplitByPageParameters>(
                    reader, params));
            PdfCopier mockCopier = mock(PdfCopier.class);
            doReturn(mockCopier).when(victim).openCopier(any(PdfReader.class), any(File.class), any(PdfVersion.class));
            doThrow(new RuntimeException()).when(mockCopier).close();
            doThrow(new OutOfMemoryError()).when(mockCopier).addPage(reader, 1);
            NotifiableTaskMetadata taskMetadata = mock(NotifiableTaskMetadata.class);
            victim.split(taskMetadata);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
