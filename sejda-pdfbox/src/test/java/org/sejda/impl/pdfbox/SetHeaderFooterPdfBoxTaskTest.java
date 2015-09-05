/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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

    @Override
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
