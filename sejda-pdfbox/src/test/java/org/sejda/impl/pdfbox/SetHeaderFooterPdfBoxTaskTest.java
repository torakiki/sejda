/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.pdfbox;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.sejda.common.ComponentsUtility;
import org.sejda.core.service.SetHeaderFooterTaskTest;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.impl.pdfbox.component.PdfTextExtractorByArea;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.task.Task;

/**
 * @author Eduard Weissmann
 */
public class SetHeaderFooterPdfBoxTaskTest extends SetHeaderFooterTaskTest {
    private PdfTextExtractorByArea extractor = new PdfTextExtractorByArea();

    public Task<SetHeaderFooterParameters> getTask() {
        return new SetHeaderFooterTask();
    }

    @Override
    protected void assertFooterHasText(String filename, int page, String expectedText) throws Exception {
        PdfSource<?> source = PdfStreamSource.newInstanceNoPassword(getResultInputStream(filename), filename);
        PDDocumentHandler handler = source.open(new DefaultPdfSourceOpener());

        try {
            assertThat(extractor.extractFooterText(handler.getPage(page)).trim(), is(expectedText));
        } finally {
            ComponentsUtility.nullSafeCloseQuietly(handler);
        }
    }

    @Override
    protected void assertHeaderHasText(String filename, int page, String expectedText) throws Exception {
        PdfSource<?> source = PdfStreamSource.newInstanceNoPassword(getResultInputStream(filename), filename);
        PDDocumentHandler handler = source.open(new DefaultPdfSourceOpener());

        try {
            assertThat(extractor.extractHeaderText(handler.getPage(page)).trim(), is(expectedText));
        } finally {
            ComponentsUtility.nullSafeCloseQuietly(handler);
        }
    }

}
