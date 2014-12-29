/*
 * Created on 24/nov/2011
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
