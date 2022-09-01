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
import org.junit.jupiter.api.Test;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageTextWriter;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.impl.sambox.component.TextWithFont;
import org.sejda.io.SeekableSources;
import org.sejda.model.encryption.NoEncryptionAtRest;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.UnsupportedTextException;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.util.IOUtils;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.font.FontMappers;
import org.sejda.sambox.pdmodel.font.FontMapping;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sejda.impl.sambox.util.FontUtils.canDisplay;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;
import static org.sejda.impl.sambox.util.FontUtils.getStandardType1Font;
import static org.sejda.impl.sambox.util.TestUtils.getTestDoc;

/**
 * @author Andrea Vacondio
 */
public class FontUtilsTest {

    private static PDFont HELVETICA = FontUtils.HELVETICA;

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

    private PDFont findFontFor(String s) {
        try {
            return FontUtils.findFontFor(new PDDocument(), s);
        } finally {
            FontUtils.clearLoadedFontCache();
        }
    }

    @Test
    public void testCanDisplay() {
        assertTrue(canDisplay("Chuck", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("कसौटी", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("Chuck", null));
    }

    @Test
    public void testFindFontFor() {
        assertNotNull(findFontFor("ทดสอบ")); // thai
        assertNotNull(findFontFor("αυτό είναι ένα τεστ")); // greek
        assertNotNull(findFontFor("വീട്")); // malayalam
        assertNotNull(findFontFor("मानक")); // hindi
        assertNotNull(findFontFor("జ")); // telugu
        assertNotNull(findFontFor("উ")); // bengali
        assertNotNull(findFontFor("עברית")); // hebrew
        assertNotNull(findFontFor("latin ąćęłńóśźż")); // latin
        assertNotNull(findFontFor("\uFFFD \u2997")); // symbols
        assertNotNull(findFontFor("Newlines\nare\r\nignored")); // newlines
        assertNotNull(findFontFor("\u2984 \u2583 \u2738 ☗⦄✸▃ ")); // symbols
        assertNotNull(findFontFor("ភាសាខ្មែរ")); // khmer
        assertNotNull(findFontFor("ጩ")); // ethiopic
        assertNotNull(findFontFor("پنجابی, ਪੰਜਾਬੀ")); // punjabi
        assertNotNull(findFontFor("தமிழ்")); // tamil
        assertNotNull(findFontFor("ગુજરાતી")); // gujarati
        assertNotNull(findFontFor("န\u103Aမာဘာသာ")); // myanmar
        assertNotNull(findFontFor("සිංහල")); // sinhalese
        assertNotNull(findFontFor("ᠮᠣᠩᠭᠣᠯ")); // mongolian
        assertNotNull(findFontFor("ಕನ್ನಡ")); // kannada
        assertNotNull(findFontFor("ଓଡ଼ିଆ ଭାଷା")); // oryia
        assertNotNull(findFontFor("ކުންފުނި")); // thaana

        // TODO: find a way to merge the armenian font into the big merged font with all others
        // so forms can be filled with latin/armenian mixed values
        // assertNotNull(findFontFor("Latin mixed with հայերէն"));
    }

    @Test
    public void fontForMultipleLanguagesInOneString() {
        assertNotNull(findFontFor(
                "န\u103Aမာဘာသာ සිංහල ગુજરાતી தமிழ் پنجابی, ਪੰਜਾਬੀ ਹਰਜੋਤ ਸਿੰਘ ភាសាខ្មែរጩ latin ąćęłńóśźż ทดสอบ വീട मानक हिन्दी ് జ উ ភាសាខ្មែរ  עברית")); // all in one
    }

    @Test
    public void roundTripWriteAndRead() throws TaskException, IOException {
        List<String> strings = Arrays.asList("ଓଡ଼ିଆଭାଷା", "ગુજરાતી ਪੰਜਾਬੀ தமிழ்", "සිංහල", "န\u103Aမာဘာသာ",
                "هذا هو كل الحق إلى اليسار", "123 יתימאה ןחבמ", "032 ציר הורמון הגדילה",
                "This is الحق  Mixed יתימאה ןחבמ",
                "ਹਰਜੋਤ ਸਿੰਘ ភាសាខ្មែរ latin ąćęłńóśźż ทดสอบ വീട मानक हिन्दी ് జ উ ☗⦄✸▃ ", "ציר הורמון הגדילה");

        for (String str : strings) {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            new PageTextWriter(doc).write(page, new Point(10, 10), str,
                    getStandardType1Font(StandardType1Font.HELVETICA), 10.0d, Color.BLACK);
            doc.addPage(page);
            try (PDDocumentHandler handler = new PDDocumentHandler(doc)) {
                File tmp = IOUtils.createTemporaryBuffer();
                handler.savePDDocument(tmp, NoEncryptionAtRest.INSTANCE);

                PDDocument doc2 = PDFParser.parse(SeekableSources.seekableSourceFrom(tmp));
                String text = new PdfTextExtractorByArea().extractTextFromArea(doc2.getPage(0),
                        new Rectangle(0, 0, 1000, 1000));
                assertEquals(noWhitespace(str), noWhitespace(text));
            }
        }
    }

    private String noWhitespace(String in) {
        return in.replaceAll("\\s", "");
    }

    @Test
    public void testFontOrFallbackPositive() {
        assertEquals(HELVETICA, fontOrFallback("Chuck", HELVETICA, new PDDocument()));
    }

    @Test
    public void testFontOrFallbackNegative() {
        assertNotNull(fontOrFallback("कसौटी", HELVETICA, new PDDocument()));
    }

    @Test
    public void testFontOrFallbackNotFoundFallback() {
        assertNull(fontOrFallback("\u1B2A\u1B35\u1B31\u1B29\u1B2E\u1B36, \u1B29\u1B32\u1B29\u1B2E\u1B36", HELVETICA,
                new PDDocument()));
    }

    @Test
    public void testCaching() {
        PDDocument doc = new PDDocument();
        PDFont expected = FontUtils.findFontFor(doc, "ทดสอบ");
        assertNotNull(expected);

        PDFont actual = FontUtils.findFontFor(doc, "ทด");
        assertSame(expected, actual, "Font is cached, same instance is returned");
    }

    @Test
    public void testCanDisplayThai() {
        assertThat(findFontFor("นี่คือการทดสอบ"), is(notNullValue()));
    }

    @Test
    public void canDisplayGeorgian() {
        assertNotNull(findFontFor("ქართული ენა"));
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
        if (!isVerdanaAvailable) {
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

    private boolean isFontAvailableOnSystem(String name) {
        FontMapping<TrueTypeFont> result = FontMappers.instance().getTrueTypeFont(name, null);
        return result != null && !result.isFallback();
    }

    @Test
    public void resolveFontsWhenTextRepeats() {
        PDDocument doc = new PDDocument();
        List<TextWithFont> textAndFonts = FontUtils.resolveFonts("123α456α789", HELVETICA, doc);

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
    public void resolveFontsWhenSpaceIsNotSeparately() {
        PDDocument doc = new PDDocument();
        List<TextWithFont> textAndFonts = FontUtils.resolveFonts("ab cd", HELVETICA, doc);

        assertThat(textAndFonts.get(0).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(0).getText(), is("ab cd"));
    }

    @Test
    public void resolveFontsWhenUnsupportedCharacters() {
        PDDocument doc = new PDDocument();
        List<TextWithFont> textAndFonts = FontUtils.resolveFonts("ab\uFE0Fcd", HELVETICA, doc);

        assertThat(textAndFonts.get(1).getFont(), is(nullValue()));
        assertThat(textAndFonts.get(1).getText(), is("\uFE0F"));

        assertThat(FontUtils.removeUnsupportedCharacters("ab \uFE0Fcd", doc), is("ab cd"));
    }

    @Test
    public void removeUnsupportedCharsDoesNotChangeStringForRTLLanguages() {
        PDDocument doc = new PDDocument();
        String text = "עברית";
        assertEquals(text, FontUtils.removeUnsupportedCharacters(text, doc));
    }

    @Test
    public void wrapping_Lines() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<String> lines = FontUtils.wrapLines(
                "This is a long line that cannot fit on a single line and could be wrapped", HELVETICA, 10, 191, doc);
        assertThat(lines,
                is(Arrays.asList("This is a long line that cannot fit on a", "single line and could be wrapped")));
    }

    @Test
    public void removingUnsupportedCharacters() {
        PDDocument doc = new PDDocument();
        String str = FontUtils.removeUnsupportedCharacters("ͷ͵ͲͲͲ Text here SǦͳͻǦʹͳͲͻ4 and here", doc);
        assertThat(str, is("͵ Text here SǦͻǦʹͻ4 and here"));
    }

    @Test
    public void wrapping_Lines_Without_Word_Break() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<String> lines = FontUtils.wrapLines(
                "This_is_a_long_line_that_cannot_fit_on_a_single_line_and_could_be_wrapped", HELVETICA, 10, 191, doc);
        assertThat(lines,
                is(Arrays.asList("This_is_a_long_line_that_cannot_fit_on_a-", "_single_line_and_could_be_wrapped")));
    }

    @Test
    public void wrapping_Lines_Without_Word_Break_Or_Other_Delimiters() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<String> lines = FontUtils.wrapLines("Thisisalonglinethatcannotfitonasinglelineandcouldbewrapped",
                HELVETICA, 10, 191, doc);
        assertThat(lines, is(Arrays.asList("Thisisalonglinethatcannotfitonasinglelinean-", "dcouldbewrapped")));
    }

    @Test
    public void wrapping_Lines_Words_Mixed_With_Super_Long_Words() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<String> lines = FontUtils.wrapLines("This is a long linethatcannotfitonasinglelineandcouldbe wrapped",
                HELVETICA, 10, 191, doc);
        assertThat(lines, is(Arrays.asList("This is a long linethatcannotfitonasingleline-", "andcouldbe wrapped")));
    }

    @Test
    public void wrappingLinesUnableToFindFont() {
        PDDocument doc = new PDDocument();
        assertThrows(UnsupportedTextException.class, () -> FontUtils.wrapLines(
                "This_is_a_long_line_that_cannot_fit_on_a_single_line_and_could_be_wrapped_հայերէն", HELVETICA, 10, 191,
                doc));
    }

    @Test
    public void brokenFontWithZeroWidthLetters() throws TaskIOException, IOException {
        PDDocument doc = getTestDoc("pdf/font-with-zero-widths.pdf");
        PDFont font = doc.getPage(0).getResources().getFont(COSName.getPDFName("F1"));
        List<String> result = FontUtils.resolveTextFragments("FRIDA", font);
        assertThat(result, is(Arrays.asList("F", "RIDA")));
    }

    @Test
    public void vectorFontWithSpace() throws TaskIOException, IOException {
        PDDocument doc = getTestDoc("pdf/font-with-vector-font.pdf");
        PDFont font = doc.getPage(0).getResources().getFont(COSName.getPDFName("F1"));

        assertThat(FontUtils.canDisplaySpace(font), is(true));
        
        List<TextWithFont> result = FontUtils.resolveFonts("31 days", font, doc);
        assertThat(result.get(0).getText(), is("31 days"));
        assertThat(result.get(0).getFont(), is(font));
    }
}
