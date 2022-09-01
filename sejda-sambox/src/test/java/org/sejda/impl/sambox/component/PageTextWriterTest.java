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

import org.junit.jupiter.api.Test;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.UnsupportedTextException;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sejda.impl.sambox.util.TestUtils.getTestDoc;
import static org.sejda.tests.TestUtils.assertPageText;

public class PageTextWriterTest {

    private PDFont helvetica = FontUtils.getStandardType1Font(StandardType1Font.HELVETICA);

    private void write(String text) throws TaskException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PageTextWriter writer = new PageTextWriter(doc);
        writer.write(page, new Point(10, 10), text, helvetica, 10d, PageTextWriter.toPDColor(Color.RED), RenderingMode.FILL, true);
    }

    @Test
    public void resolveTextAndFontsWhenTextRepeats() throws TaskException {
        write("123α456α789");
    }

    @Test
    public void resolvedSpaceSeparately() throws TaskException {
        write("ab cd");
    }

    @Test
    public void throwsWhenCharacterUnsupported() {
        assertThrows(UnsupportedTextException.class, () -> write("\uFE0F"));
    }
    
    @Test
    public void brokenFontWithZeroWidthLetters() throws TaskIOException, IOException {
        PDDocument doc = getTestDoc("pdf/font-with-zero-widths.pdf");

        PDFont font = doc.getPage(0).getResources().getFont(COSName.getPDFName("F1"));
        PDPage page = new PDPage();
        PageTextWriter textWriter = new PageTextWriter(doc);
        textWriter.write(page, new Point2D.Double(10d, 10d), 
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890", font, 12d, Color.RED);

        assertPageText(page, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
    }
}
