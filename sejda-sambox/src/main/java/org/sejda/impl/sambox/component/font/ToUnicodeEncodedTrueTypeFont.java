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
package org.sejda.impl.sambox.component.font;

import org.apache.fontbox.cmap.CMap;
import org.sejda.sambox.pdmodel.font.PDTrueTypeFont;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A view of a simple TrueType font of the document that can write text the font itself refuses to encode.
 * <p>
 * {@link PDTrueTypeFont} encodes text through glyph names. Some producers embed TrueType subsets as symbolic fonts
 * without an /Encoding, whose font program has no glyph names (post table format 3) and only a (1,0) cmap keyed by
 * character code. Nothing can be encoded by name in such a font, not even the text it already displays, so text
 * written with it ends up in a fallback font. The font's /ToUnicode map is the only link between character codes and
 * text: this class encodes through its inverse.
 * <p>
 * It shares the font dictionary of the original font, so text written with it references the same font object in
 * the document.
 */
public class ToUnicodeEncodedTrueTypeFont extends PDTrueTypeFont {

    private final Map<Integer, Integer> codePointToCode = new HashMap<>();

    public ToUnicodeEncodedTrueTypeFont(PDTrueTypeFont font) throws IOException {
        super(font.getCOSObject());

        CMap toUnicode = getToUnicodeCMap();
        if (toUnicode == null || !toUnicode.hasUnicodeMappings()) {
            return;
        }

        for (int code = 0; code <= 255; code++) {
            String unicode = toUnicode.toUnicode(code);
            if (unicode == null || unicode.isEmpty() || unicode.codePointCount(0, unicode.length()) != 1) {
                continue;
            }
            int codePoint = unicode.codePointAt(0);
            // first code wins, when several codes show the same character
            if (!codePointToCode.containsKey(codePoint) && hasGlyph(code, codePoint)) {
                codePointToCode.put(codePoint, code);
            }
        }
    }

    // subsets can keep codes around that have no glyph, or map to an empty one
    private boolean hasGlyph(int code, int codePoint) {
        try {
            if (codeToGID(code) == 0) {
                return false;
            }
            if (Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint)) {
                return getWidth(code) > 0;
            }
            GeneralPath path = getPath(code);
            return path != null && path.getBounds2D().getWidth() > 0;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    @Override
    protected byte[] encode(int unicode) throws IOException {
        Integer code = codePointToCode.get(unicode);
        if (code != null) {
            return new byte[] { (byte) code.intValue() };
        }
        return super.encode(unicode);
    }
}
