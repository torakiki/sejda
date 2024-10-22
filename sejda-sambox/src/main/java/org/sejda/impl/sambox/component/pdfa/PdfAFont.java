/*
 * Created on 15/10/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.sambox.cos.COSString;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDTrueTypeFont;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Andrea Vacondio
 */
abstract class PdfAFont {
    //For every font embedded in a conforming file and used for rendering, the glyph width information in
    //the font dictionary and in the embedded font program shall be consistent
    private boolean wrongWidth;
    private final PDFont font;
    private String name;

    PdfAFont(PDFont font, String name) {
        Objects.requireNonNull(font);
        this.font = font;
        this.name = name;
    }

    abstract void addString(COSString string);

    static PdfAFont getInstance(PDFont font, String name) {
        return switch (font) {
            case PDTrueTypeFont ttf -> new TrueTypePdfAFont(ttf, name);
            default -> new DummyPdfAFont(font, name);
        };
    }

    PDFont font() {
        return font;
    }

    boolean wrongWidth() {
        return wrongWidth;
    }

    void wrongWidth(boolean wrongWidth) {
        this.wrongWidth = wrongWidth;
    }

    String name() {
        return name;
    }

    abstract void regenerateFontWidths() throws IOException;
}
