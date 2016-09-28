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
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.font.PDFont;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class PageTextWriterTest {

    private PDFont helvetica = FontUtils.getStandardType1Font(StandardType1Font.HELVETICA);

    @Test
    public void resolveTextAndFontsWhenTextRepeats() throws TaskIOException {
        PageTextWriter writer = new PageTextWriter(new PDDocument());
        List<PageTextWriter.TextWithFont> textAndFonts = writer.resolveFonts("123α456α789", helvetica);

        assertThat(textAndFonts.get(0).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(0).getText(), is("123"));

        assertThat(textAndFonts.get(1).getFont().getName(), is(not("Helvetica")));
        assertThat(textAndFonts.get(1).getText(), is("α"));

        assertThat(textAndFonts.get(2).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(2).getText(), is("456"));

        assertThat(textAndFonts.get(3).getFont().getName(), is(not("Helvetica")));
        assertThat(textAndFonts.get(3).getText(), is("α"));
    }

    @Test
    public void resolvedSpaceSeparately() throws TaskIOException {
        PageTextWriter writer = new PageTextWriter(new PDDocument());
        List<PageTextWriter.TextWithFont> textAndFonts = writer.resolveFonts("ab cd", helvetica);

        assertThat(textAndFonts.get(0).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(0).getText(), is("ab"));

        assertThat(textAndFonts.get(1).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(1).getText(), is(" "));

        assertThat(textAndFonts.get(2).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(2).getText(), is("cd"));
    }


}