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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.sejda.fonts.OptionalUnicodeType0Font;
import org.sejda.fonts.UnicodeType0Font;
import org.sejda.model.pdf.FontResource;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.font.PDFont;
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
            LOG.info("Text cannot be written with font {}, using fallback", font.getName());
            return fallbackSupplier.get();
        }
        return font;
    }

    private static PDFont loadFont(PDDocument document, FontResource font) {
        InputStream in = font.getFontStream();
        try {
            PDType0Font loaded = PDType0Font.load(document, in);
            LOG.trace("Loaded font {}", loaded.getName());
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
                LOG.debug("Found suitable font {}", loaded.getName());
                return loaded;
            }
        }
        return null;
    }

    /**
     * @param text
     * @param font
     * @return true if the given font can display the given text
     */
    public static boolean canDisplay(String text, PDFont font) {
        try {
            if (nonNull(font)) {
                font.encode(text);
                return true;
            }
        } catch (IllegalArgumentException | IOException e) {
            // nothing
        }
        return false;
    }
}
