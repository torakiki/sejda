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

import static java.util.Objects.nonNull;

import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.sejda.fonts.OptionalUnicodeType0Font;
import org.sejda.fonts.UnicodeType0Font;
import org.sejda.model.pdf.FontResource;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.font.FontMappers;
import org.sejda.sambox.pdmodel.font.FontMapping;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDFontDescriptor;
import org.sejda.sambox.pdmodel.font.PDType0Font;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to map from Sejda font definition to PDFBox.
 * 
 * @author Andrea Vacondio
 * 
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
     * check the label can be written with the selected font, use the fallback otherwise
     * 
     * @param text
     * @param font
     * @param fallbackSupplier
     * @return
     */
    public static PDFont fontOrFallback(String text, PDFont font, Supplier<PDFont> fallbackSupplier) {
        if (nonNull(fallbackSupplier) && !canDisplay(text, font)) {
            PDFont fallback = fallbackSupplier.get();
            String fallbackName = fallback == null ? null : fallback.getName();
            LOG.info("Text '{}' cannot be written with font {}, using fallback {}", text, font.getName(), fallbackName);
            return fallback;
        }
        return font;
    }

    // caches fonts, PER DOCUMENT
    // has no auto-magical way to clear the cache when doc processing is done
    // if you use this in a long lived process, call the cache clear method to avoid leaking memory
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

        InputStream in = font.getFontStream();
        try {
            PDType0Font loaded = PDType0Font.load(document, in);
            LOG.trace("Loaded font {}", loaded.getName());
            docCache.put(font.getResource(), loaded);
            return loaded;
        } catch (IOException e) {
            LOG.warn("Failed to load font " + font, e);
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 
     * @param document
     * @param text
     * @return a font capable of displaying the given string or null
     */
    public static final PDFont findFontFor(PDDocument document, String text) {
        try {
            // lets make sure the jar is in the classpath
            Class.forName("org.sejda.fonts.UnicodeType0Font");
            PDFont found = findFontAmong(document, text, UnicodeType0Font.values());
            if (nonNull(found)) {
                return found;
            }
            Class.forName("org.sejda.fonts.OptionalUnicodeType0Font");
            return findFontAmong(document, text, OptionalUnicodeType0Font.values());

        } catch (ClassNotFoundException clf) {
            LOG.warn("Fallback fonts not available");
        }
        return null;
    }

    private static PDFont findFontAmong(PDDocument document, String text, FontResource... fonts) {
        for (FontResource font : fonts) {
            PDFont loaded = loadFont(document, font);
            if (canDisplay(text, loaded)) {
                LOG.debug("Found suitable font {} to display '{}'", loaded, text);
                return loaded;
            }
        }
        return null;
    }

    /**
     * Check is given text contains only unicode whitespace characters
     *
     * @param text
     * @return
     */
    public static boolean isOnlyWhitespace(String text) {
        return text.replaceAll("\\p{Zs}", "").length() == 0;
    }

    /**
     * Removes all unicode whitespace characters from the input string
     *
     * @param text
     * @return
     */
    public static String removeWhitespace(String text) {
        return text.replaceAll("\\p{Zs}", "");
    }

    public static boolean canDisplaySpace(PDFont font) {
        try {
            font.encode(" ");
            return true;
        } catch (IllegalArgumentException | IOException | UnsupportedOperationException e) {
            // Nope
        }
        return false;
    }

    /**
     * Returns true if the given font can display the given text. IMPORTANT: Ignores all whitespace in text.
     */
    public static boolean canDisplay(String text, PDFont font) {
        if (font == null)
            return false;

        // LOG.debug("Can display '{}' using {}?", text, font);

        try {
            // remove all whitespace characters and check only if those can be written using the font
            byte[] encoded = font.encode(removeWhitespace(text));

            if (font instanceof PDType0Font) {
                InputStream in = new ByteArrayInputStream(encoded);
                while (in.available() > 0) {
                    int code = font.readCode(in);

                    // LOG.debug("Read codePoint {}", code);

                    PDType0Font type0Font = (PDType0Font) font;
                    GeneralPath path = type0Font.getPath(code);
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
         *
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
}
