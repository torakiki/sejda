/*
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

import org.junit.Test;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.UnsupportedTextException;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.font.PDFont;

import java.awt.*;


public class PageTextWriterTest {

    private PDFont helvetica = FontUtils.getStandardType1Font(StandardType1Font.HELVETICA);

    private void write(String text) throws TaskIOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PageTextWriter writer = new PageTextWriter(doc);
        writer.write(page, new Point(10, 10), text, helvetica, 10d, Color.BLACK);
    }

    @Test
    public void resolveTextAndFontsWhenTextRepeats() throws TaskIOException {
        write("123α456α789");
    }

    @Test
    public void resolvedSpaceSeparately() throws TaskIOException {
        write("ab cd");
    }

    @Test(expected = UnsupportedTextException.class)
    public void throwsWhenCharacterUnsupported() throws TaskIOException {
        write("\uFE0F");
    }
}