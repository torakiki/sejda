/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;
import static org.sejda.sambox.util.BidiUtils.visualToLogical;

import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.sejda.impl.sambox.component.TextWithFont;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.UnsupportedTextException;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.font.FontResource;
import org.sejda.model.pdf.font.Type0FontsProvider;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.FontMappers;
import org.sejda.sambox.pdmodel.font.FontMapping;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDFontDescriptor;
import org.sejda.sambox.pdmodel.font.PDSimpleFont;
import org.sejda.sambox.pdmodel.font.PDType0Font;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.font.PDType3CharProc;
import org.sejda.sambox.pdmodel.font.PDType3Font;
import org.sejda.sambox.pdmodel.font.PDVectorFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to map from Sejda font definition to PDFBox.
 *
 * @author Andrea Vacondio
 */
public final class FontUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FontUtils.class);

    private FontUtils() {
        // hide
    }

    private static final Map<StandardType1Font, PDType1Font> STANDARD_TYPE1_FONTS;

    static {
        Map<StandardType1Font, PDType1Font> fontsCache = new EnumMap<>(StandardType1Font.class);
        fontsCache.put(StandardType1Font.CURIER, PDType1Font.COURIER);
        fontsCache.put(StandardType1Font.CURIER_BOLD, PDType1Font.COURIER_BOLD);
        fontsCache.put(StandardType1Font.CURIER_BOLD_OBLIQUE, PDType1Font.COURIER_BOLD_OBLIQUE);
        fontsCache.put(StandardType1Font.CURIER_OBLIQUE, PDType1Font.COURIER_OBLIQUE);
        fontsCache.put(StandardType1Font.HELVETICA, PDType1Font.HELVETICA);
        fontsCache.put(StandardType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA_BOLD);
        fontsCache.put(StandardType1Font.HELVETICA_BOLD_OBLIQUE, PDType1Font.HELVETICA_BOLD_OBLIQUE);
        fontsCache.put(StandardType1Font.HELVETICA_OBLIQUE, PDType1Font.HELVETICA_OBLIQUE);
        fontsCache.put(StandardType1Font.SYMBOL, PDType1Font.SYMBOL);
        fontsCache.put(StandardType1Font.ZAPFDINGBATS, PDType1Font.ZAPF_DINGBATS);
        fontsCache.put(StandardType1Font.TIMES_BOLD, PDType1Font.TIMES_BOLD);
        fontsCache.put(StandardType1Font.TIMES_BOLD_ITALIC, PDType1Font.TIMES_BOLD_ITALIC);
        fontsCache.put(StandardType1Font.TIMES_ITALIC, PDType1Font.TIMES_ITALIC);
        fontsCache.put(StandardType1Font.TIMES_ROMAN, PDType1Font.TIMES_ROMAN);
        STANDARD_TYPE1_FONTS = Collections.unmodifiableMap(fontsCache);
    }

    public static PDFont HELVETICA = PDType1Font.HELVETICA;

    public static final FontResource[] TYPE0FONTS;

    static {
        TYPE0FONTS = stream(ServiceLoader.load(Type0FontsProvider.class).spliterator(), false)
                .flatMap(p -> p.getFonts().stream()).sorted(Comparator.comparingInt(FontResource::priority))
                .toArray(FontResource[]::new);
    }

    /**
     * Mapping between Sejda and PDFBox standard type 1 fonts implementation
     *
     * @param st1Font
     * @return the PDFBox font.
     */
    public static PDType1Font getStandardType1Font(StandardType1Font st1Font) {
        return STANDARD_TYPE1_FONTS.get(st1Font);
    }

    /**
     * Checks the text can be written with the given font, find a fallback font otherwise
     */
    public static PDFont fontOrFallback(String text, PDFont font, PDDocument document) {
        if (!canDisplay(text, font)) {
            PDFont fallback = findFontFor(document, text);
            String fallbackName = fallback == null ? null : fallback.getName();
            LOG.debug("Text '{}' cannot be written with font {}, using fallback {}", text, font.getName(),
                    fallbackName);
            return fallback;
        }
        return font;
    }

    // caches fonts, PER DOCUMENT
    // has no auto-magical way to clear the cache when doc processing is done
    // if you use this in a long lived process, call the cache clear method to avoid leaking memory
    // if we get some issue we could consider something like com.twelvemonkeys.util.WeakWeakMap<K, V>
    private static Map<PDDocument, Map<String, PDFont>> loadedFontCache = new HashMap<>();

    public static void clearLoadedFontCache() {
        loadedFontCache.clear();
    }

    public static void clearLoadedFontCache(PDDocument document) {
        loadedFontCache.remove(document);
    }

    public static PDFont loadFont(PDDocument document, FontResource font) {
        if (!loadedFontCache.containsKey(document)) {
            loadedFontCache.put(document, new HashMap<>());
        }

        Map<String, PDFont> docCache = loadedFontCache.get(document);
        if (docCache.containsKey(font.getResource())) {
            return docCache.get(font.getResource());
        }

        try (InputStream in = font.getFontStream()) {
            PDType0Font loaded = PDType0Font.load(document, in);
            LOG.trace("Loaded font {}", loaded.getName());
            docCache.put(font.getResource(), loaded);
            return loaded;
        } catch (IOException e) {
            LOG.warn("Failed to load font " + font, e);
            return null;
        }
    }

    /**
     * @param document
     * @param text
     * @return a font capable of displaying the given string or null
     */
    public static final PDFont findFontFor(PDDocument document, String text) {
        for (FontResource font : TYPE0FONTS) {
            PDFont loaded = loadFont(document, font);
            if (canDisplay(text, loaded)) {
                LOG.debug("Found suitable font {} to display '{}'", loaded, text);
                return loaded;
            }
        }
        return null;
    }

    /**
     * @param text
     * @return true if given text contains only unicode whitespace characters
     */
    public static boolean isOnlyWhitespace(String text) {
        return text.replaceAll("\\p{Zs}", "").length() == 0;
    }

    /**
     * Removes all unicode whitespace characters from the input string
     *
     * @param text
     * @return the resulting string
     */
    public static String removeWhitespace(String text) {
        return text.replaceAll("\\p{Zs}", "").replaceAll("\\r\\n", "").replaceAll("\\n", "");
    }

    public static boolean canDisplaySpace(PDFont font) {
        try {
            // try encode
            font.encode(" ");

            // see if width is non zero
            return font.getStringWidth(" ") > 0;
        } catch (IllegalArgumentException | IOException | UnsupportedOperationException | NullPointerException e) {
            // Nope
        }
        return false;
    }

    /**
     * Returns true if the given font can display the given text. IMPORTANT: Ignores all whitespace in text.
     */
    public static boolean canDisplay(String text, PDFont font) {
        return canDisplayString(removeWhitespace(text), font);
    }

    private static boolean canDisplayString(String text, PDFont font) {
        if (font == null)
            return false;

        // LOG.debug("Can display '{}' using {}?", text, font);

        try {
            // remove all whitespace characters and check only if those can be written using the font
            byte[] encoded = font.encode(text);

            if (font instanceof PDVectorFont) {
                InputStream in = new ByteArrayInputStream(encoded);
                while (in.available() > 0) {
                    int code = font.readCode(in);

                    // LOG.debug("Read codePoint {}", code);

                    PDVectorFont vectorFont = (PDVectorFont) font;
                    GeneralPath path = vectorFont.getPath(code);
                    // if(path != null) {
                    // LOG.debug("GeneralPath is {} for '{}' (code = {}, font = {})", path.getBounds2D(), new String(Character.toChars(code)), code, font.getName());
                    // }

                    if (path == null || path.getBounds2D().getWidth() == 0) {
                        return false;
                    }
                }
            }

            return true;
        } catch (IllegalArgumentException | IOException | UnsupportedOperationException | NullPointerException e) {
            // LOG.debug("Cannot display text with font", e);
        }
        return false;
    }

    public static double calculateBBoxHeight(String text, PDFont font) {
        requireNotNullArg(font, "Font cannot be null");
        double maxHeight = 0;
        try {
            InputStream in = new ByteArrayInputStream(font.encode(text));
            while (in.available() > 0) {
                int code = font.readCode(in);
                if (font instanceof PDType3Font) {
                    maxHeight = Math.max(maxHeight,
                            ofNullable(((PDType3Font) font).getCharProc(code)).map(PDType3CharProc::getGlyphBBox)
                                    .map(PDRectangle::toGeneralPath).map(p -> p.getBounds2D().getHeight()).orElse(0d));
                } else if (font instanceof PDVectorFont) {
                    maxHeight = Math.max(maxHeight, ofNullable(((PDVectorFont) font).getPath(code))
                            .map(p -> p.getBounds2D().getHeight()).orElse(0d));
                } else if (font instanceof PDSimpleFont) {
                    PDSimpleFont simpleFont = (PDSimpleFont) font;
                    String name = ofNullable(simpleFont.getEncoding()).map(e -> e.getName(code)).orElse(null);
                    if (nonNull(name)) {
                        maxHeight = Math.max(maxHeight, simpleFont.getPath(name).getBounds2D().getHeight());
                    }
                }
            }
        } catch (IOException e) {
            LOG.warn("An error occured while calculating the highest glyph bbox", e);
        }
        return maxHeight;
    }

    public static boolean isBold(PDFont font) {
        String lowercasedName = font.getName().toLowerCase();
        return lowercasedName.contains("bold");
    }

    public static boolean isItalic(PDFont font) {
        String lowercasedName = font.getName().toLowerCase();
        return lowercasedName.contains("italic") || lowercasedName.contains("oblique");
    }

    /**
     * Helper for subset fonts. Determines if a font is subset, computes original font name. Provides methods for loading the original full font from the system, if available, or
     * loading a fallback font.
     */
    public static class FontSubsetting {
        public final String fontName;
        public final boolean isSubset;
        public final PDFont subsetFont;

        public FontSubsetting(PDFont subsetFont) {
            this.subsetFont = subsetFont;

            // is it a subset font? ABCDEF+Verdana
            String fontName = StringUtils.trimToEmpty(subsetFont.getName());
            String[] fontNameFragments = fontName.split("\\+");

            if (fontNameFragments.length == 2 && fontNameFragments[0].length() == 6) {
                this.isSubset = true;
                this.fontName = fontNameFragments[1];
            } else {
                this.isSubset = false;
                this.fontName = null;
            }
        }

        public PDFont loadOriginalOrSimilar(PDDocument document) {
            PDFont original = loadOriginal(document);
            if (original == null) {
                return loadSimilar(document);
            }
            return original;
        }

        /**
         * Tries to load the original full font from the system
         */
        public PDFont loadOriginal(PDDocument document) {
            String lookupName = fontName.replace("-", " ");

            LOG.debug("Searching the system for a font matching name '{}'", lookupName);

            FontMapping<TrueTypeFont> fontMapping = FontMappers.instance().getTrueTypeFont(lookupName, null);
            if (fontMapping != null && fontMapping.getFont() != null && !fontMapping.isFallback()) {
                TrueTypeFont mappedFont = fontMapping.getFont();

                try {
                    LOG.debug("Original font available on the system: {}", fontName);
                    return PDType0Font.load(document, mappedFont.getOriginalData());
                } catch (IOException ioe) {
                    LOG.warn("Failed to load font from system", ioe);
                    try {
                        mappedFont.close();
                    } catch (IOException e) {
                        LOG.warn("Failed closing font", e);
                    }
                }
            }

            return null;
        }

        /**
         * Tries to load a similar full font from the system
         */
        public PDFont loadSimilar(PDDocument document) {
            String lookupName = fontName.replace("-", " ");

            // Eg: Arial-BoldMT
            PDFontDescriptor descriptor = new PDFontDescriptor(new COSDictionary());
            descriptor.setFontName(fontName.split("-")[0]);
            descriptor.setForceBold(FontUtils.isBold(subsetFont));
            descriptor.setItalic(FontUtils.isItalic(subsetFont));

            LOG.debug(
                    "Searching the system for a font matching name '{}' and description [name:{}, bold:{}, italic:{}]",
                    lookupName, descriptor.getFontName(), descriptor.isForceBold(), descriptor.isItalic());

            FontMapping<TrueTypeFont> fontMapping = FontMappers.instance().getTrueTypeFont(lookupName, descriptor);
            if (fontMapping != null && fontMapping.getFont() != null) {
                TrueTypeFont mappedFont = fontMapping.getFont();

                try {
                    if (fontMapping.isFallback()) {
                        LOG.debug("Fallback font available on the system: {} (for {})", mappedFont.getName(), fontName);
                    } else {
                        LOG.debug("Original font available on the system: {}", fontName);
                    }

                    return PDType0Font.load(document, mappedFont.getOriginalData());
                } catch (IOException ioe) {
                    LOG.warn("Failed to load font from system", ioe);
                    try {
                        mappedFont.close();
                    } catch (Exception e) {
                        LOG.warn("Failed closing font", e);
                    }
                }
            }

            return null;
        }
    }

    /**
     * Wraps the given text on multiple lines, if it does not fit within the given maxWidth It will try to determine if all text can be written with given font and find a fallback
     * for parts that are not supported.
     */
    public static List<String> wrapLines(String rawLabel, PDFont font, float fontSize, double maxWidth,
            PDDocument document) throws TaskIOException {
        List<String> lines = new ArrayList<>();

        String label = org.sejda.commons.util.StringUtils.normalizeWhitespace(rawLabel);

        StringBuilder currentString = new StringBuilder();
        double currentWidth = 0;

        List<TextWithFont> resolvedStringsToFonts = FontUtils.resolveFonts(label, font, document);

        for (TextWithFont stringAndFont : resolvedStringsToFonts) {
            try {

                PDFont resolvedFont = stringAndFont.getFont();
                String resolvedLabel = stringAndFont.getText();

                if (isNull(resolvedFont)) {
                    throw new UnsupportedTextException(
                            "Unable to find suitable font for string \"" + resolvedLabel + "\"",
                            resolvedLabel);
                }

                String[] words = visualToLogical(resolvedLabel).split("(?<=\\b)");
                for (String word : words) {
                    double textWidth = getSimpleStringWidth(word, resolvedFont, fontSize);

                    if (textWidth > maxWidth || word.length() > 10) {
                        // this is a giant word that has no breaks and exceeds max width

                        // check for each char if it can be added to current line, wrap on new line if not
                        Iterator<Integer> codePointIterator = word.codePoints().iterator();
                        while (codePointIterator.hasNext()) {
                            int codePoint = codePointIterator.next();

                            String ch = new String(Character.toChars(codePoint));
                            double chWidth = getSimpleStringWidth(ch, resolvedFont, fontSize);
                            if (currentWidth + chWidth > maxWidth) {
                                currentString.append("-");
                                lines.add(currentString.toString().trim());
                                currentString = new StringBuilder();
                                currentWidth = 0;
                            }

                            currentWidth += chWidth;
                            currentString.append(ch);
                        }
                    } else {
                        // regular scenario: check if word can be added to current line, wrap on new line if not
                        if (currentWidth + textWidth > maxWidth) {
                            lines.add(currentString.toString().trim());
                            currentString = new StringBuilder();
                            currentWidth = 0;
                        }

                        currentWidth += textWidth;
                        currentString.append(word);
                    }
                }

            } catch (IOException e) {
                throw new TaskIOException(e);
            }
        }

        if (!currentString.toString().isEmpty()) {
            lines.add(currentString.toString().trim());
        }

        return lines;
    }

    /**
     * Calculates the width of the string using the given font. Does not try to find out if the text can actually be written with the given font and find fallback
     */
    public static double getSimpleStringWidth(String text, PDFont font, double fontSize) throws IOException {
        double textWidth = font.getStringWidth(text) / 1000 * fontSize;

        // sometimes the string width is reported incorrectly, too small. when writing ' ' (space) it leads to missing spaces.
        // use the largest value between font average width and text string width
        // TODO: replace zero with heuristic based "small value"
        if (textWidth == 0) {
            textWidth = font.getAverageFontWidth() / 1000 * fontSize;
        }

        return textWidth;
    }

    /**
     * Supports writing labels which require multiple fonts (eg: mixing thai and english words) Returns a list of text with associated font.
     */
    public static List<TextWithFont> resolveFonts(String label, PDFont font, PDDocument document) {
        PDFont currentFont = font;
        StringBuilder currentString = new StringBuilder();

        // we want to keep the insertion order
        List<TextWithFont> result = new ArrayList<>();
        Iterator<Integer> codePointIterator = visualToLogical(label).codePoints().iterator();
        while (codePointIterator.hasNext()) {
            int codePoint = codePointIterator.next();

            String s = new String(Character.toChars(codePoint));

            PDFont f = fontOrFallback(s, font, document);
            if (s.equals(" ")) {
                // we want space to be a separate text item
                // because some fonts are missing the space glyph
                // so we'll handle it separate from the other chars

                // some fonts don't have glyphs for space.
                // figure out if that's the case and switch to a standard font as fallback
                if (!FontUtils.canDisplaySpace(f)) {
                    f = FontUtils.getStandardType1Font(StandardType1Font.HELVETICA);
                }

                if (f != currentFont) {
                    // end current string, before space
                    if (currentString.length() > 0) {
                        result.add(new TextWithFont(currentString.toString(), currentFont));
                    }

                    // add space
                    result.add(new TextWithFont(" ", f));
                    currentString = new StringBuilder();
                    currentFont = f;
                } else {
                    currentString.append(s);
                }
            } else if (currentFont == f) {
                currentString.append(s);
            } else {
                if (currentString.length() > 0) {
                    result.add(new TextWithFont(currentString.toString(), currentFont));
                }

                currentString = new StringBuilder(s);
                currentFont = f;
            }
        }

        result.add(new TextWithFont(currentString.toString(), currentFont));

        for (TextWithFont each : result) {
            LOG.trace("Will write '{}' with {}", each.getText(), each.getFont());
        }

        return result;
    }

    /**
     * Splits an input string into multiple fragments, when glyphs with 0 width are detected
     */
    public static List<String> resolveTextFragments(String input, PDFont font) {
        List<String> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        
        for(int codePoint: input.codePoints().toArray()){
            try {
                if(font.getWidth(codePoint) == 0) {
                    if(current.size() > 0) {
                        StringBuilder s = new StringBuilder();
                        current.stream().map(Character::toChars).forEach(s::append);
                        result.add(s.toString());
                    }
                    
                    result.add(new String(Character.toChars(codePoint)));
                    current = new ArrayList<>();
                } else {
                    current.add(codePoint);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(current.size() > 0) {
            StringBuilder s = new StringBuilder();
            current.stream().map(Character::toChars).forEach(s::append);
            result.add(s.toString());
        }
        
        return result;
    }

    public static String removeUnsupportedCharacters(String text, PDDocument doc) {
        return replaceUnsupportedCharacters(text, doc, "");
    }

    public static String replaceUnsupportedCharacters(String text, PDDocument doc, String replacement) {
        List<TextWithFont> resolved = resolveFonts(text, HELVETICA, doc);
        Set<String> unsupported = new HashSet<>();
        resolved.forEach(tf -> {
            if (tf.getFont() == null) {
                unsupported.add(tf.getText());
            }
        });

        // replace unsupported groups of text longer ones first
        // eg: first replace "ääç" and then "ä"
        List<String> unsupportedSortedByLength = new ArrayList<>(unsupported);
        unsupportedSortedByLength.sort((o1, o2) -> new Integer(o2.length()).compareTo(o1.length()));

        String result = text;
        for (String s : unsupportedSortedByLength) {
            result = result.replaceAll(Pattern.quote(s), replacement);
        }

        return result;
    }
}
