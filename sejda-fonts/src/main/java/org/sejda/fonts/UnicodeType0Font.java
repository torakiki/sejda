/*
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
package org.sejda.fonts;

import java.io.InputStream;

import org.sejda.model.pdf.FontResource;

public enum UnicodeType0Font implements FontResource {
    NOTO_SANS_REGULAR("/fonts/sans/NotoSans-Regular.ttf"),
    NOTO_SANS_KUFI_REGULAR("/fonts/sans/NotoKufiArabic-Regular.ttf"),
    NOTO_SANS_NASKH_REGULAR("/fonts/sans/NotoNaskhArabic-Regular.ttf"),
    NOTO_SANS_DEVANAGARI_REGULAR("/fonts/sans/NotoSansDevanagari-Regular.ttf"),
    NOTO_SANS_HEBREW_REGULAR("/fonts/sans/NotoSansHebrew-Regular.ttf"),
    NOTO_SANS_THAI_REGULAR("/fonts/sans/NotoSansThai-Regular.ttf"),
    NOTO_SANS_TELUGU_REGULAR("/fonts/sans/NotoSansTelugu-Regular.ttf");

    private String resource;

    UnicodeType0Font(String resource) {
        this.resource = resource;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public InputStream getFontStream() {
        return this.getClass().getResourceAsStream(resource);
    }
}
