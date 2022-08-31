/*
 * Created on 07 mar 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Andrea Vacondio
 *
 */
public class FilenameFooterWriterTest {

    @Test
    public void write() throws TaskException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        new FilenameFooterWriter(true, doc).addFooter(page, "My Footer", 20);
        assertThat(new PdfTextExtractorByArea().extractFooterText(page).trim(), is("My Footer 20"));
    }

    @Test
    public void write_long_filename_that_needs_truncation() throws TaskException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        new FilenameFooterWriter(true, doc).addFooter(page, "My very long title that will not fit on the page and needs to be truncated so that it will not overflow and cover the page number and generally look not so nice", 20);
        assertPageFooterText(page,"My very long title that will not fit on the page and needs to be truncated so that it will not overflow and cover the page number a20");
    }

    @Test
    public void dontWrite() throws TaskException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        new FilenameFooterWriter(false, doc).addFooter(page, "My Footer", 20);
        assertThat(new PdfTextExtractorByArea().extractFooterText(page).trim(), isEmptyOrNullString());
    }

    @Test
    public void write_filename_contains_bad_characters() throws TaskException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        String withBadCharacter = "This is a bad \uF021character";
        FilenameFooterWriter writer = new FilenameFooterWriter(true, doc);
        writer.addFooter(page, withBadCharacter, 1);

        assertPageFooterText(page, "This is a bad #character 1");
    }

    private void assertPageFooterText(PDPage page, String expectedText) throws TaskIOException {
        assertThat(new PdfTextExtractorByArea().extractFooterText(page).trim(), is(expectedText));
    }
}
