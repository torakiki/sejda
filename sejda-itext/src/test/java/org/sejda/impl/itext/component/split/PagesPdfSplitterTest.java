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
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.parameter.SimpleSplitParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.NotifiableTaskMetadata;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * @author Andrea Vacondio
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleBookmark.class)
public class PagesPdfSplitterTest {

    private SimpleSplitParameters params;
    private PdfReader reader;

    @Before
    public void setUp() {
        params = mock(SimpleSplitParameters.class);
        reader = mock(PdfReader.class);
        PdfSource source = mock(PdfSource.class);
        when(source.getName()).thenReturn("name");
        when(params.getSource()).thenReturn(source);
        when(reader.getNumberOfPages()).thenReturn(10);
        when(params.getPages(anyInt())).thenReturn(Collections.singleton(5));
        PowerMockito.mockStatic(SimpleBookmark.class, Answers.RETURNS_SMART_NULLS.get());
    }

    @Test(expected = OutOfMemoryError.class)
    // issue #80
    public void testFinallyDoesntSwallowErrors() throws IOException, TaskException {
        PagesPdfSplitter<SimpleSplitParameters> victim = spy(new PagesPdfSplitter<SimpleSplitParameters>(reader, params));
        PdfCopier mockCopier = mock(PdfCopier.class);
        doReturn(mockCopier).when(victim).openCopier(any(PdfReader.class), any(File.class), any(PdfVersion.class));
        doThrow(new RuntimeException()).when(mockCopier).close();
        doThrow(new OutOfMemoryError()).when(mockCopier).addPage(reader, 1);
        NotifiableTaskMetadata taskMetadata = mock(NotifiableTaskMetadata.class);
        victim.split(taskMetadata);
    }
}
