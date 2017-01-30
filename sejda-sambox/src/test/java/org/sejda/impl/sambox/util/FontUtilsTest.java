/*
 * Created on 13/nov/2012
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
package org.sejda.impl.sambox.util;

import org.apache.fontbox.ttf.TrueTypeFont;
import org.junit.Test;
import org.sejda.fonts.UnicodeType0Font;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.font.FontMappers;
import org.sejda.sambox.pdmodel.font.FontMapping;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;

import java.io.IOException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.sejda.impl.sambox.util.FontUtils.*;

/**
 * @author Andrea Vacondio
 */
public class FontUtilsTest {

    @Test
    public void testGetStandardType1Fontg() {
        assertEquals(PDType1Font.COURIER, getStandardType1Font(StandardType1Font.CURIER));
        assertEquals(PDType1Font.COURIER_BOLD, getStandardType1Font(StandardType1Font.CURIER_BOLD));
        assertEquals(PDType1Font.COURIER_BOLD_OBLIQUE, getStandardType1Font(StandardType1Font.CURIER_BOLD_OBLIQUE));
        assertEquals(PDType1Font.COURIER_OBLIQUE, getStandardType1Font(StandardType1Font.CURIER_OBLIQUE));
        assertEquals(PDType1Font.HELVETICA, getStandardType1Font(StandardType1Font.HELVETICA));
        assertEquals(PDType1Font.HELVETICA_BOLD, getStandardType1Font(StandardType1Font.HELVETICA_BOLD));
        assertEquals(PDType1Font.HELVETICA_BOLD_OBLIQUE,
                getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE));
        assertEquals(PDType1Font.HELVETICA_OBLIQUE, getStandardType1Font(StandardType1Font.HELVETICA_OBLIQUE));
        assertEquals(PDType1Font.TIMES_BOLD, getStandardType1Font(StandardType1Font.TIMES_BOLD));
        assertEquals(PDType1Font.TIMES_BOLD_ITALIC, getStandardType1Font(StandardType1Font.TIMES_BOLD_ITALIC));
        assertEquals(PDType1Font.TIMES_ITALIC, getStandardType1Font(StandardType1Font.TIMES_ITALIC));
        assertEquals(PDType1Font.TIMES_ROMAN, getStandardType1Font(StandardType1Font.TIMES_ROMAN));
        assertEquals(PDType1Font.SYMBOL, getStandardType1Font(StandardType1Font.SYMBOL));
        assertEquals(PDType1Font.ZAPF_DINGBATS, getStandardType1Font(StandardType1Font.ZAPFDINGBATS));
    }

    @Test
    public void testCanDisplay() {
        assertTrue(canDisplay("Chuck", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("कसौटी", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("Chuck", null));
    }

    @Test
    public void testFindFontFor() {
        assertEquals("NotoSansThai", findFontFor(new PDDocument(), "ทดสอบ").getName());
        assertEquals("NotoSans", findFontFor(new PDDocument(), "αυτό είναι ένα τεστ").getName());
        assertNull(findFontFor(new PDDocument(), "വീട്"));
    }

    @Test
    public void testFontOrFallbackPositive() {
        PDType1Font expected = getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE);
        assertEquals(expected, fontOrFallback("Chuck", expected, () -> getStandardType1Font(StandardType1Font.CURIER)));
    }

    @Test
    public void testFontOrFallbackNegative() {
        PDType1Font expected = getStandardType1Font(StandardType1Font.CURIER);
        assertEquals(expected, fontOrFallback("कसौटी", getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE),
                () -> expected));
    }

    @Test
    public void testFontOrFallbackNullSipplier() {
        PDType1Font expected = getStandardType1Font(StandardType1Font.CURIER);
        assertEquals(expected, fontOrFallback("कसौटी", expected, null));
    }

    @Test
    public void testCaching() {
        PDDocument doc = new PDDocument();
        PDFont expected = FontUtils.findFontFor(doc, "ทดสอบ");

        PDFont actual = findFontFor(doc, "ทด");
        assertTrue("Font is cached, same instance is returned", expected == actual);
    }

    @Test
    public void testCanDisplayThai() {
        PDFont noto = FontUtils.loadFont(new PDDocument(), UnicodeType0Font.NOTO_SANS_THAI_REGULAR);
        assertThat(FontUtils.canDisplay("นี่คือการทดสอบ", noto), is(true));
    }

    @Test
    public void canDisplayGeorgian() {
        PDFont font = FontUtils.findFontFor(new PDDocument(), "ქართული ენა");
        assertNotNull("No font available for Georgian", font);
        assertThat(font.getName(), is("NotoSansGeorgian"));
    }

    @Test
    public void testCanDisplayType0FontsThatDontThrow() throws TaskIOException, IOException {
        PDDocument doc = getTestDoc("pdf/2-up-sample.pdf");

        PDResources res = doc.getPage(0).getResources();
        PDFormXObject form = (PDFormXObject) res.getXObject(COSName.getPDFName("Form2"));
        PDResources formRes = form.getResources();
        PDFont font = formRes.getFont(COSName.getPDFName("F0"));

        assertThat(font.getName(), is("Arial-BoldMT"));
        assertThat(FontUtils.canDisplay("Redacted out :)", font), is(false));
    }

    @Test
    public void testLoadingFullFontFromSystemForSubsetFonts() throws TaskIOException, IOException {
        boolean isVerdanaAvailable = isFontAvailableOnSystem("Verdana");
        if(!isVerdanaAvailable) {
            return;
        }

        PDDocument doc = getTestDoc("pdf/subset-font.pdf");

        PDResources res = doc.getPage(0).getResources();
        PDFormXObject form = (PDFormXObject) res.getXObject(COSName.getPDFName("Xf1"));
        PDResources formRes = form.getResources();
        PDFont font = formRes.getFont(COSName.getPDFName("F1"));
        assertThat(font.getName(), is("PXAAAA+Verdana"));

        PDFont original = new FontUtils.FontSubsetting(font).loadOriginal(doc);
        // relies on Verdana font being present on the system
        assertThat(original.getName(), is("Verdana"));
    }

    private PDDocument getTestDoc(String name) throws TaskIOException {
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream(name), randomAlphanumeric(16) + ".pdf");

        return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
    }

    private boolean isFontAvailableOnSystem(String name) {
        FontMapping<TrueTypeFont> result =  FontMappers.instance().getTrueTypeFont(name, null);
        return result != null && !result.isFallback();
    }
}
